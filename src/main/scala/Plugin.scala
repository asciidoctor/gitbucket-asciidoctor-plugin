import scala.util.Try

import org.slf4j.LoggerFactory

import gitbucket.core.plugin.PluginRegistry
import gitbucket.core.service.SystemSettingsService.SystemSettings
import io.github.gitbucket.solidbase.model.Version
import javax.servlet.ServletContext
import tobiasroeser.gitbucket.asciidoctor.AsciidoctorRenderer

class Plugin extends gitbucket.core.plugin.Plugin {

  private[this] val log = LoggerFactory.getLogger(classOf[Plugin])

  override val pluginId: String = "asciidoctor"
  override val pluginName: String = "AsciiDoctor Plugin"
  override val description: String = "Provides AsciiDoc rendering for GitBucket."
  override val versions: List[Version] = List(
    new Version("1.0.1"),
    new Version("1.0.2")
  )

  private[this] var renderer: Option[AsciidoctorRenderer] = None

  override def initialize(registry: PluginRegistry, context: ServletContext, settings: SystemSettings): Unit = {

    log.info("About to initialize Asciidoctor")

    val test = Try { new AsciidoctorRenderer() }
    log.info("Result: " + test)
    val asciidoc = test.get

    log.info("Registering AsciidoctorRenderer for various extensions")
    registry.addRenderer("adoc", asciidoc)
    registry.addRenderer("asciidoc", asciidoc)
    registry.addRenderer("ad", asciidoc)

    renderer = Option(asciidoc)

  }

  override def shutdown(registry: PluginRegistry, context: ServletContext, settings: SystemSettings): Unit = {
    renderer.map(r => r.shutdown())
  }

}
