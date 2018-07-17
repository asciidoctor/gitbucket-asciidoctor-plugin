package tobiasroeser.gitbucket.asciidoctor

import java.io.File
import java.net.URI
import java.util

import gitbucket.core.service.{AccountService, RepositoryService}
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.util.{JGitUtil, StringUtil}
import gitbucket.core.util.Directory._
import gitbucket.core.util.SyntaxSugars._
import org.asciidoctor.ast.DocumentRuby
import org.asciidoctor.extension.{IncludeProcessor, PreprocessorReader}
import org.eclipse.jgit.api.Git
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class AsciidoctorJgitIncludeProcessor(config: java.util.Map[String, Object]) extends IncludeProcessor(config)
  with RepositoryService with AccountService{
  val logger = LoggerFactory.getLogger(getClass)

  override def handles(target: String): Boolean = {
    true
  }

  override def process(document: DocumentRuby, reader: PreprocessorReader, target: String, attributes: util.Map[String, AnyRef]): Unit = {
    val documentPath = URI.create(document.getAttr("gitbucket-path").toString)
    val repository = document.getAttr("gitbucket-repository").asInstanceOf[RepositoryInfo]
    val branch = document.getAttr("gitbucket-branch").toString
    val targetPath = documentPath.resolve(target)

    using(Git.open(getRepositoryDir(repository.owner, repository.name))) { git =>
      val revCommit = JGitUtil.getRevCommitFromId(git, git.getRepository.resolve(branch))
      JGitUtil.getContentFromPath(git, revCommit.getTree, targetPath.toString, true).map{ bytes =>
        val content = StringUtil.convertFromByteArray(bytes)
        val embed = if(attributes.asScala.contains("lines")){
          val lines = attributes.get("lines").toString
          val linesRe = """(\d+)\.\.(\d+)""".r
          lines match {
            case linesRe(start, end) =>
              content.split("""\r?\n""").slice(start.toInt - 1, end.toInt).mkString("\n")
          }
        }else{
          content
        }
        reader.push_include(embed, target, target, 1, attributes)
      }
    }
  }
}
