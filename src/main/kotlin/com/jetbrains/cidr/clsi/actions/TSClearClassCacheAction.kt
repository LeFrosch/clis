package com.jetbrains.cidr.clsi.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.jetbrains.cidr.clsi.bindings.TSClassService

class TSClearClassCacheAction : AnAction(), DumbAware {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        service<TSClassService>().clearCache(project)
    }
}