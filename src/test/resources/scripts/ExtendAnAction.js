let AnAction = Java.type('com.intellij.openapi.actionSystem.AnAction')
let AnActionAdapter = Java.extend(AnAction)

new AnActionAdapter("fooAction", {})
