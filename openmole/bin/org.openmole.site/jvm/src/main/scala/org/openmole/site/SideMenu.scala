package org.openmole.site

/*
 * Copyright (C) 23/06/17 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.openmole.site
import tools._
import stylesheet._

import scalatags.Text.TypedTag
import scalatags.Text.all._

object Link {
  def intern(name: String) = s"#${name.replaceAll(" ", "")}"
}

case class Link(name: String, link: String)

case class SideMenu(links: Seq[Link], menuStyle: AttrPair = classIs(""), preText: String = "", otherTab: Boolean = false) {
  def insert(ls: Seq[Link]) = copy(ls ++ links)

  def toBlock = SideMenuBlock(Seq(this))
}

case class SideMenuBlock(menus: Seq[SideMenu]) {

  def insert(sideMenu: SideMenu): SideMenuBlock = copy(sideMenu +: menus)

  def insert(sideMenu: Option[SideMenu]): SideMenuBlock = {
    sideMenu match {
      case Some(sm: SideMenu) ⇒ insert(sm)
      case _                  ⇒ this
    }
  }

  def insert(links: Seq[Link], menuNumber: Int = 0) = {
    if (menus.size > menuNumber) copy(menus.updated(menuNumber, menus(menuNumber).insert(links)))
    else this
  }

  def add(sideMenu: SideMenu) = copy(menus :+ sideMenu)

  def add(sideMenu: Option[SideMenu]): SideMenuBlock = add(sideMenu.getOrElse(SideMenu(Seq())))

  private def build(topDiv: TypedTag[_]) =
    div(
      topDiv(
        for {
          m ← menus
        } yield {
          div(
            if (m.links.isEmpty) div else div(m.preText, fontWeight := "bold", paddingTop := 20),
            for {
              p ← m.links
            } yield {
              div(paddingTop := 5)(linkButton(p.name, p.link, m.menuStyle, m.otherTab))
            }
          )
        }
      )
    )

  val right = build(div(rightDetailButtons(220), id := "sidebar-right"))

  val left = build(div(leftDetailButtons(220), id := "sidebar-left"))

}

object SideMenu {

  implicit def pageToLink(p: Page): Link = Link(p.name, p.file)

  implicit def seqPagToSeqLink(ps: Seq[Page]): Seq[Link] = ps.map {
    pageToLink
  }

  def details(pages: Seq[Page]) = SideMenu(pages, classIs(btn ++ btn_default), otherTab = true)

  def fromStrings(title: String, stringMenus: String*) =
    SideMenu(preText = title, links = stringMenus.map { a ⇒ Link(a, Link.intern(a)) })

  val model = SideMenu(DocumentationPages.modelPages, classIs(btn ++ btn_primary), "Available tasks").toBlock

  val method = SideMenu(DocumentationPages.methodPages, classIs(btn ++ btn_primary), "Available methods").toBlock

  val environment = SideMenu(DocumentationPages.environmentPages, classIs(btn ++ btn_primary), "Available environments").toBlock

  val more = SideMenu(
    Seq(
      DocumentationPages.language,
      DocumentationPages.gui,
      DocumentationPages.advancedConcepts
    ), classIs(btn ++ btn_default), "See also", true
  ).toBlock

  lazy val menus = Map(
    DocumentationPages.advancedSampling.name -> Seq(advancedSamplingMenu.toBlock.left, SideMenu.more.right),
    DocumentationPages.netLogoGA.name -> Seq(SideMenu.gaWithNetlogoMenu.toBlock.left),
    DocumentationPages.capsule.name -> Seq(SideMenu.capsuleMenu.toBlock.left),
    DocumentationPages.source.name -> Seq(SideMenu.sourceMenu.toBlock.left),
    DocumentationPages.hook.name -> Seq(SideMenu.hookMenu.toBlock.left),
    DocumentationPages.gui.name -> Seq(SideMenu.guiGuide.toBlock.left),
    DocumentationPages.console.name -> Seq(SideMenu.consoleMenu.toBlock.left),
    DocumentationPages.howToContribute.name -> Seq(SideMenu.howToContributeMenu.toBlock.left),
    DocumentationPages.nativePackaging.name -> Seq(SideMenu.nativePackagingMenu.toBlock.left),
    Pages.gettingStarted.name -> Seq(SideMenu.more.right),
    Pages.faq.name -> Seq(SideMenu.faqMenu.toBlock.left)
  )

  lazy val faqMenu = fromStrings(
    "",
    shared.faq.javaVersion,
    shared.faq.oldVersions,
    shared.faq.sshConnectionBug,
    shared.faq.passwordAuthentication,
    shared.faq.isOpenMOLEup,
    shared.faq.homeQuota,
    shared.faq.sampleError,
    shared.faq.cannotGetCare,
    shared.faq.tooManyOpenFiles,
    shared.faq.qxcbConnection,
    shared.faq.pathOverFile,
    shared.faq.notListed
  )

  lazy val guiGuide = fromStrings(
    "Contents",
    shared.guiGuide.overview,
    shared.guiGuide.startProject,
    shared.guiGuide.fileManagment,
    shared.guiGuide.playAndMonitor,
    shared.guiGuide.authentication,
    shared.guiGuide.plugin
  )

  lazy val clusterMenu = fromStrings(
    "Contents",
    shared.clusterMenu.pbsTorque,
    shared.clusterMenu.sge,
    shared.clusterMenu.slurm,
    shared.clusterMenu.condor,
    shared.clusterMenu.oar
  )

  lazy val nativeMenu = fromStrings(
    "Contents",
    shared.nativeModel.rExample,
    shared.nativeModel.pythonExample,
    shared.nativeModel.advancedOptions
  )

  lazy val nativePackagingMenu = fromStrings(
    "Contents",
    shared.nativePackagingMenu.introCARE,
    shared.nativePackagingMenu.advancedOptions,
    shared.nativePackagingMenu.localResources,
    shared.nativePackagingMenu.localExecutable,
    shared.nativePackagingMenu.troubleshooting
  )

  lazy val directSamplingMenu = fromStrings(
    "Contents",
    shared.directSamplingMenu.gridSampling,
    shared.directSamplingMenu.uniformSampling

  )

  lazy val netlogoMenu = fromStrings(
    "Contents",
    shared.netlogoMenu.simulation,
    shared.netlogoMenu.doe,
    shared.netlogoMenu.task,
    shared.netlogoMenu.storing,
    shared.netlogoMenu.together,
    shared.netlogoMenu.further
  )

  lazy val otherDoEMenu = fromStrings(
    "Contents",
    shared.otherDoEMenu.csvSampling,
    shared.otherDoEMenu.LHSSobol,
    shared.otherDoEMenu.severalInputs,
    shared.otherDoEMenu.sensitivityAnalysis,
    shared.otherDoEMenu.sensitivityFireModel
  )

  lazy val dataProcessingMenu = fromStrings(
    "Contents",
    shared.dataProcessingMenu.setOfFiles,
    shared.dataProcessingMenu.pathsVsFiles,
    shared.dataProcessingMenu.example,
    shared.dataProcessingMenu.further
  )

  lazy val advancedSamplingMenu = fromStrings(
    "Contents",
    shared.advancedSamplingMenu.sampling,
    shared.advancedSamplingMenu.combineSampling,
    shared.advancedSamplingMenu.zipSampling,
    shared.advancedSamplingMenu.filterSampling,
    shared.advancedSamplingMenu.randomSampling,
    shared.advancedSamplingMenu.higherLevelSampling,
    shared.advancedSamplingMenu.isKeyword
  )

  lazy val consoleMenu = fromStrings(
    "Contents",
    shared.consoleMenu.authentication,
    shared.consoleMenu.run
  )

  lazy val hookMenu = fromStrings(
    "Contents",
    shared.hookMenu.plugHook,
    shared.hookMenu.appendToFileHook,
    shared.hookMenu.csvHook,
    shared.hookMenu.copyFileHook,
    shared.hookMenu.toStringHook,
    shared.hookMenu.displayHook
  )

  lazy val sourceMenu = fromStrings(
    "Contents",
    shared.sourceMenu.plugSource,
    shared.sourceMenu.listFiles,
    shared.sourceMenu.listDirectories,
    shared.sourceMenu.example
  )

  lazy val capsuleMenu = fromStrings(
    "Contents",
    shared.capsuleMenu.definition,
    shared.capsuleMenu.strainer,
    shared.capsuleMenu.master
  )

  lazy val gaWithNetlogoMenu = fromStrings(
    "Contents",
    shared.gaWithNetlogo.antModel,
    shared.gaWithNetlogo.defineProblem,
    shared.gaWithNetlogo.runOpeMOLE,
    shared.gaWithNetlogo.optimizationAlgo,
    shared.gaWithNetlogo.scaleUp
  )

  lazy val howToContributeMenu = fromStrings(
    "Contents",
    shared.howToContributeMenu.prerequisites,
    shared.howToContributeMenu.firstTimeSetup,
    shared.howToContributeMenu.buildAppFromSources,
    shared.howToContributeMenu.standaloneArchive,
    shared.howToContributeMenu.compileDocker,
    shared.howToContributeMenu.buildWebsite,
    shared.howToContributeMenu.webpagesSources,
    shared.howToContributeMenu.repositories,
    shared.howToContributeMenu.projectOrganization,
    shared.howToContributeMenu.devVersion,
    shared.howToContributeMenu.bugReport,
    shared.howToContributeMenu.contributionProcedure,
    shared.howToContributeMenu.branchingModel
  )

}