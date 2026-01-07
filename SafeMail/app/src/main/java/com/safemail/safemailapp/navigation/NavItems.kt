package com.safemail.safemailapp.navigation


sealed class NavItem(val route: String) {
  //  object Splash : NavItem("splash")
    object Signup : NavItem("signup")
    object Login : NavItem("login")
    object Home : NavItem("home")
    object News : NavItem("news")
    object ReadLater : NavItem("read_later")

    // FIX: Change "Employee" to "employee"
    object Employee: NavItem("employee")

    object TaskHub : NavItem("task_hub")
    object Todo : NavItem("todo_screen")
    object StickyNotes : NavItem("notes_screen")
    object AdminInfo : NavItem("admin_info")
}
/*
sealed class NavItem(val route: String) {
   object Splash : NavItem("splash")
    object Signup : NavItem("signup")
   object Login : NavItem("login")
    object Home : NavItem("home")
    object News : NavItem( "news")
    object ReadLater : NavItem("read_later")
    object Employee: NavItem("Employee")

    object TaskHub : NavItem("task_hub")
    object Todo : NavItem("todo_screen")
    object StickyNotes : NavItem("notes_screen")
    object AdminInfo : NavItem("admin_info")

}*/