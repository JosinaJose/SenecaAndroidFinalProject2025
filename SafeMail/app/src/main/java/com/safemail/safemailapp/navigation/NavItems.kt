package com.safemail.safemailapp.navigation




sealed class NavItem(val route: String) {
   object Splash : NavItem("splash")
    object Signup : NavItem("Signup")
   object Login : NavItem("Login")
    object Home : NavItem("home")
    object News : NavItem( "News")

}