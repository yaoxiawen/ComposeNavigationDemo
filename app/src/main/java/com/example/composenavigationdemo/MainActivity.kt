package com.example.composenavigationdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.composenavigationdemo.ui.theme.ComposeNavigationDemoTheme

const val URI = "my-app://my.example.app"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeNavigationDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHostDemo()
                    //Navigation搭配底部导航栏
//                    NavBottomBar()
                }
            }
        }
    }


    @Composable
    fun NavHostDemo() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = RouteConfig.ROUTE_PAGE_ONE) {
            //普通跳转无参数
            composable(RouteConfig.ROUTE_PAGE_ONE) {
                PageOne(navController)
            }
            //必传参数，使用"/"拼写在路由地址后面添加占位符
            composable("${RouteConfig.ROUTE_PAGE_TWO}/{${ParamsConfig.PARAMS_NAME}}/{${ParamsConfig.PARAMS_AGE}}",
                arguments = listOf(
                    navArgument(ParamsConfig.PARAMS_NAME) {},//参数是String类型可以不用额外指定，这句不写也是可以的
                    navArgument(ParamsConfig.PARAMS_AGE) {
                        type = NavType.IntType //指定具体类型
                        defaultValue = 25 //默认值（选配）
                        nullable = false  //可否为null（选配）
                    }
                )
            ) {
                //通过composable函数中提供的NavBackStackEntry提取参数
                val argument = requireNotNull(it.arguments)
                val name = argument.getString(ParamsConfig.PARAMS_NAME)
                val age = argument.getInt(ParamsConfig.PARAMS_AGE)
                PageTwo(name, age, navController)
            }
            //可选参数，使用"?argName={argName}&argName2={argName2}"拼接，跟浏览器地址栏的可选参数一样，第一个用?拼接，后续用&拼接
            composable("${RouteConfig.ROUTE_PAGE_THREE}?${ParamsConfig.PARAMS_NAME}={${ParamsConfig.PARAMS_NAME}}&${ParamsConfig.PARAMS_AGE}={${ParamsConfig.PARAMS_AGE}}",
                arguments = listOf(
                    navArgument(ParamsConfig.PARAMS_NAME) {
                        nullable = true
                    },
                    navArgument(ParamsConfig.PARAMS_AGE) {
                        type = NavType.IntType //指定具体类型
                        defaultValue = 25 //默认值（选配）
                        nullable = false  //可否为null（选配）
                    }
                )) {
                //通过composable函数中提供的NavBackStackEntry提取参数
                val argument = requireNotNull(it.arguments)
                val name = argument.getString(ParamsConfig.PARAMS_NAME)
                val age = argument.getInt(ParamsConfig.PARAMS_AGE)
                PageThree(name, age, navController)
            }
            //深度链接 DeepLink
            composable("${RouteConfig.ROUTE_PAGE_FOUR}?${ParamsConfig.PARAMS_NAME}={${ParamsConfig.PARAMS_NAME}}&${ParamsConfig.PARAMS_AGE}={${ParamsConfig.PARAMS_AGE}}",
                arguments = listOf(
                    navArgument(ParamsConfig.PARAMS_NAME) {
                        nullable = true
                    },
                    navArgument(ParamsConfig.PARAMS_AGE) {
                        type = NavType.IntType //指定具体类型
                        defaultValue = 25 //默认值（选配）
                        nullable = false  //可否为null（选配）
                    }
                ),
                deepLinks = listOf(navDeepLink {
                    uriPattern = "$URI/{${ParamsConfig.PARAMS_NAME}}/{${ParamsConfig.PARAMS_AGE}}"
                })
            ) {
                //通过composable函数中提供的NavBackStackEntry提取参数
                val argument = requireNotNull(it.arguments)
                val name = argument.getString(ParamsConfig.PARAMS_NAME)
                val age = argument.getInt(ParamsConfig.PARAMS_AGE)
                PageFour(name, age, navController)
            }
        }
    }

    @Composable
    fun PageOne(navController: NavController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White
                )
        ) {
            Text(text = "这是页面1")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                //普通跳转
//                navController.navigate(RouteConfig.ROUTE_PAGE_TWO)
                //携带参数跳转，必传参数必须传，不传会crash
                navController.navigate("${RouteConfig.ROUTE_PAGE_TWO}/this is name/12")
            }) {
                Text(
                    text = "跳转页面2",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }


    @Composable
    fun PageTwo(name: String?, age: Int, navController: NavController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White
                )
        ) {
            Text(text = "这是页面2")
            Text(text = "name:$name,age:$age")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                // 在进入RouteConfig.ROUTE_PAGE_THREE之前，回退栈会弹出所有的可组合项，直到 RouteConfig.ROUTE_PAGE_ONE
//                navController.navigate(RouteConfig.ROUTE_PAGE_THREE) {
//                    popUpTo(RouteConfig.ROUTE_PAGE_ONE)
//                }
                // 在进入RouteConfig.ROUTE_PAGE_THREE之前，回退栈会弹出所有的可组合项，直到 RouteConfig.ROUTE_PAGE_ONE，并且包括它
//                navController.navigate(RouteConfig.ROUTE_PAGE_THREE) {
//                    popUpTo(RouteConfig.ROUTE_PAGE_ONE) { inclusive = true }
//                }
                // 对应 Android 的 SingleTop，如果回退栈顶部已经是 RouteConfig.ROUTE_PAGE_THREE，就不会重新创建
//                navController.navigate(RouteConfig.ROUTE_PAGE_THREE) {
//                    launchSingleTop = true
//                }
                //携带可选参数跳转
                navController.navigate("${RouteConfig.ROUTE_PAGE_THREE}?${ParamsConfig.PARAMS_NAME}=demo&${ParamsConfig.PARAMS_AGE}=15")
            }) {
                Text(
                    text = "跳转页面3",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    fun PageThree(name: String?, age: Int, navController: NavController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White
                )
        ) {
            Text(text = "这是页面3")
            Text(text = "name:$name,age:$age")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
//                navController.navigateUp()    //返回上一级界面
//                navController.popBackStack()  //可以指定返回的界面（不指定就相当于navigateUp()）。
                //深度链接匹配跳转
                navController.navigate("$URI/deeplink/123".toUri())
            }) {
                Text(
                    text = "跳转页面4",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    fun PageFour(name: String?, age: Int, navController: NavController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White
                )
        ) {
            Text(text = "这是页面4")
            Text(text = "name:$name,age:$age")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                navController.navigateUp()    //返回上一级界面
//                navController.popBackStack()  //可以指定返回的界面（不指定就相当于navigateUp()）。
            }) {
                Text(
                    text = "返回",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}