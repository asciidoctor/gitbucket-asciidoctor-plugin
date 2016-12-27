package tobiasroeser.gitbucket.asciidoctor

import org.asciidoctor.Asciidoctor
import org.asciidoctor.AttributesBuilder
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.htmlcleaner.HtmlCleaner
import org.htmlcleaner.HtmlNode
import org.htmlcleaner.SimpleHtmlSerializer
import org.htmlcleaner.TagNode
import org.htmlcleaner.TagNodeVisitor
import org.slf4j.LoggerFactory
import java.util.Properties
import java.io.File
import collection.JavaConverters._

import gitbucket.core.controller.Context
import gitbucket.core.plugin.RenderRequest
import gitbucket.core.plugin.Renderer
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.view.helpers
import gitbucket.core.util.Directory.GitBucketHome
import play.twirl.api.Html

class AsciidoctorRenderer extends Renderer {

  private[this] val log = LoggerFactory.getLogger(classOf[AsciidoctorRenderer])

  private[this] var _asciidoctor: Option[Asciidoctor] = None

  private[this] def asciidoctor: Asciidoctor = _asciidoctor match {
    case None =>
      log.info("About to create Asciidoctor")
      _asciidoctor = Option(Asciidoctor.Factory.create(getClass().getClassLoader()))
      _asciidoctor.get
    case Some(a) => a
  }

  def shutdown(): Unit = {
    _asciidoctor.map(_.shutdown())
  }

  def render(request: RenderRequest): Html = {
    import request._
    Html(toHtml(filePath, fileContent, branch, repository, enableWikiLink, enableRefsLink)(context))
  }

  def toHtml(filePath: List[String], asciidoc: String, branch: String, repository: RepositoryInfo,
    enableWikiLink: Boolean, enableRefsLink: Boolean)(implicit context: Context): String = {

    log.info("About to render Asciidoctor")

    val options = OptionsBuilder.options()
    options.safe(SafeMode.SECURE)
    val attributes = AttributesBuilder.attributes()
    attributes.showTitle(true)
    attributes.attribute("env", "gitbucket")
    attributes.attribute("env-gitbucket", true)
    attributes.attribute("outfilesuffix", ".adoc")
    attributes.attribute("gitbucket-branch", branch)

    val asciidocAttributes = new File(GitBucketHome, "/asciidoctor.properties")
    val propsJava = new Properties();
    if(asciidocAttributes.exists){
      val in = new java.io.FileInputStream(asciidocAttributes)
      propsJava.load(in)
      in.close()
    }

    val props = propsJava.stringPropertyNames().asScala
    props.foreach(k => {
      attributes.attribute(k, propsJava.getProperty(k))
    })

    options.attributes(attributes.get())
    val rendered = asciidoctor.render(asciidoc, options)

    val path = filePath.reverse.tail.reverse match {
      case Nil => ""
      case p => p.mkString("", "/", "/")
    }
    val relativeUrlPrefix = s"${helpers.url(repository)}/blob/${branch}/${path}"
    val relativeImageUrlPrefix = s"${helpers.url(repository)}/raw/${branch}/${path}"
    prefixRelativeUrls(rendered, relativeUrlPrefix, relativeImageUrlPrefix)
  }

  private[this] val exceptionPrefixes = Seq("#", "/", "http://", "https://")

  def prefixRelativeUrls(html: String, urlPrefix: String, imageUrlPrefix: String): String = {
    val cleaner = new HtmlCleaner()
    val node = cleaner.clean(html)
    node.traverse(new TagNodeVisitor() {
      override def visit(tagNode: TagNode, htmlNode: HtmlNode): Boolean = {
        htmlNode match {
          case tag: TagNode if tag.getName == "a" =>
            Option(tag.getAttributeByName("href")) foreach { href =>
              if (exceptionPrefixes.forall(p => !href.startsWith(p))) {
                tag.addAttribute("href", s"${urlPrefix}${href}")
              }
            }
          case tag: TagNode if tag.getName == "img" =>
            Option(tag.getAttributeByName("src")) foreach { src =>
              if (exceptionPrefixes.forall(p => !src.startsWith(p))) {
                tag.addAttribute("src", s"${imageUrlPrefix}${src}")
              }
            }
          case _ =>
        }
        // continue traversal
        true
      }
    })
    new SimpleHtmlSerializer(cleaner.getProperties()).getAsString(node)
  }
}