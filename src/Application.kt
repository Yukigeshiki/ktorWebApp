package com.testWebApp

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.css.*
import kotlinx.html.*

data class IndexData(val items: List<Int>)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

  val client = HttpClient(Apache) {
  }

  install(FreeMarker) {
    templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
  }

  routing {
    get("/") {
      call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
    }

    get("/html-dsl") {
      call.respondHtml {
        body {
          h1 { +"HTML" }
          ul {
            for (n in 1..10) {
              li { +"$n" }
            }
          }
        }
      }
    }

    get("/styles.css") {
      call.respondCss {
        body {
          backgroundColor = Color.red
        }
        p {
          fontSize = 2.em
        }
        rule("p.myclass") {
          color = Color.blue
        }
      }
    }

    get("/{...}") {
      call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3, 4, 5))), ""))
    }

    static("/static") {
      resources("static")
    }
  }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
  style(type = ContentType.Text.CSS.toString()) {
    +CSSBuilder().apply(builder).toString()
  }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
  this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
  this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
