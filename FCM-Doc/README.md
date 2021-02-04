# FCM（Firebase Cloud Message）推送

## Firebase介绍

Firebase是一家实时后端数据库公司，提供多种产品帮助开发者快速构建App功能。2014年被Google收购。

* IaaS（Infrastructure as a Service）基础设施即服务：提供基础设施。如阿里云
* PaaS（Platform as a Service）平台即服务：提供软件开发能力的平台，如涂鸦开发者平台
* SaaS（Software as a Service）软件即服务：提供软件开发，俗称软件外包
* BaaS（Backend as a Service）后端即服务：提供后台云服务、云存储，简化移动应用开发，如Firebase、七牛云、友盟等

支持的功能：有免费和付费

* [Firebase开发文档](https://firebase.google.com/docs)
* [Firebase产品和功能](https://console.firebase.google.com/project/smarthome-8684b/features?hl=zh_cn)
* [Firebase依赖库](https://firebase.google.com/docs/android/setup#available-libraries)

![Firebase产品和功能](./Firebase-Function.png)

相关概念：[Firebase说明](https://firebase.google.com/docs/projects/learn-more)、[Android和Firebase更多说明](https://firebase.google.com/docs/android/learn-more)

* Firebase产品：Firebase提供的功能或服务
* Firebase项目：一个项目中可以有多个应用（iOS、Android、Web等），同一项目中的资源共享
* Firebase应用：不同平台应用，同一平台不同包名应用
* Google Cloud：Firebase项目实际是Google Cloud项目，具有Firebase特定配置和服务。GCM->FCM
* Firebase控制台：管理Firebase项目、应用、产品等
* 谷歌三件套，功能边界
  * Google Play服务框架：Android的后台服务，提供常用的Google API调用，如Google登录服务、Google地图服务等
  * Google Play商店：Google应用商店，依赖Google Play服务
  * Google Play游戏服务：提供手机游戏开发的API，不安装的话可能无法正常运行Google Play商店的游戏

## 架构

![架构](./diagram-FCM.png)

![](./push_flow.jpeg)

## 消息类型

|          | 通知消息               | 数据消息（透传消息） | 通知+数据              |
| -------- | ---------------------- | -------------------- | ---------------------- |
| 接收方式 | 由SDK处理              | 由应用自行处理       |                        |
| 数据格式 | 通知title、body        | 自定义键值对         | 通知内容+数据内容      |
| 发送方式 | 控制台发送、服务器发送 | 服务器发送           | 控制台发送、服务器发送 |

客户端接收消息

| 应用状态 | 通知                | 数据                | 通知+数据                                                  |
| :------- | :------------------ | :------------------ | :--------------------------------------------------------- |
| 前台     | `onMessageReceived` | `onMessageReceived` | `onMessageReceived`                                        |
| 后台     | 系统任务栏显示      | `onMessageReceived` | 通知：系统任务栏显示，数据：通知栏跳转intent的extras属性。 |



## 接入流程

**需要系统带GMS**

[实操Demo](https://github.com/Afauria/PushSample)

### 手动添加Firebase

1. 控制台创建Firebase项目

2. Firebase项目添加应用，填入应用包名

3. 下载`google-services.json`文件，添加到application模块中

4. Gradle添加google服务插件

   ```groovy
   //项目build.gradle
   buildscript {
     repositories {
       google()  // Google's Maven repository
     }
   
     dependencies {
       classpath 'com.google.gms:google-services:4.3.4'  // Google Services plugin
     }
   }
   
   allprojects {
     repositories {
       google()  // Google's Maven repository
     }
   }
   ```

5. 模块引入插件（`app/build.gradle`）

   ```groovy
   apply plugin: 'com.google.gms.google-services'  // Google Services plugin
   ```

6. 模块添加依赖

   ```groovy
   //1.使用BoM，可以通过仅指定一个版本（BoM的版本）来管理所有Firebase库版本。
   dependencies {
       // Import the BoM for the Firebase platform
       implementation platform('com.google.firebase:firebase-bom:26.2.0')
       implementation 'com.google.firebase:firebase-messaging-ktx'
       implementation 'com.google.firebase:firebase-analytics-ktx'//analytic可以移除，用于演示BoM作用
   }
   //2.不使用BoM
   dependencies {
       implementation 'com.google.firebase:firebase-messaging-ktx:21.0.1'
       implementation 'com.google.firebase:firebase-analytics-ktx:18.0.0'//analytic可以移除
   }
   ```

7. 创建一个Service继承`FirebaseMessagingService`，Manifest注册服务

   ```xml
   <service
       android:name=".MyFirebaseMessagingService"
       android:exported="false">
       <intent-filter>
           <action android:name="com.google.firebase.MESSAGING_EVENT" />
       </intent-filter>
   </service>
   ```

8. 获取令牌：

   * 主动获取：`FirebaseMessaging.getInstance().getToken()`
   * Token刷新回调：onNewToken，应用卸载安装等会触发token刷新

9. 模拟发送下行消息（下发给客户端，上行消息：客户端发给服务端）：可以指定应用、指定主题、指定设备组、指定设备token等将消息发送给特定用户或群体

   * 控制台发送
   * REST API调用
   * 后台应用不同语言接入服务端SDK，调用SDK方法



### 使用Android Studio Firebase Assistant添加

FIrebase Assistant是一个Android Studio插件，可创建并关联到Firebase项目，自动添加配置

使用方式：`AndroidStudio->Tools->Firebase`打开Assistant工具，按照工具流程添加

存在问题：导入库版本不一定是最新的，可能和Android Studio版本有关。低版本库getToken方式不一样，需要手动更新版本。



## 获取Firebase服务授权

服务器向Firebase服务发送请求需要获取授权。[有三种方式](https://firebase.google.com/docs/cloud-messaging/auth-server)

- Google 应用默认凭据（ADC，Google Cloud Application Default Credentials）：在Google服务器环境运行，可以直接使用默认凭据
- 服务帐号 JSON 文件：在非Google服务器环境运行，需要下载服务账号JSON文件。
  1. 配置到服务器环境变量中。
  2. 通过代码读取Json文件路径，可能存在凭据泄漏风险，推荐方式一
- 源自服务帐号的短期有效的 OAuth 2.0 访问令牌：根据服务账号JSON文件，生成临时访问令牌，添加到发送请求中。如果使用Admin SDK，SDK内部会自行处理，不需要手动生成。

生成访问令牌：

* 下载服务账号json文件
* 下载脚本，修改脚本
* 脚本和json文件放在同一目录，进入目录执行脚本

```python
import argparse
import json
import requests

# pip install oauth2client
from oauth2client.service_account import ServiceAccountCredentials

PROJECT_ID = 'smarthome-8684b'
BASE_URL = 'https://fcm.googleapis.com'
FCM_ENDPOINT = 'v1/projects/' + PROJECT_ID + '/messages:send'
FCM_URL = BASE_URL + '/' + FCM_ENDPOINT
SCOPES = ['https://www.googleapis.com/auth/firebase.messaging']


# [START retrieve_access_token]
def _get_access_token():
    """Retrieve a valid access token that can be used to authorize requests.
    :return: Access token.
    """
    credentials = ServiceAccountCredentials.from_json_keyfile_name(
        'service-account.json', SCOPES)
    access_token_info = credentials.get_access_token()
    print access_token_info.access_token
    return access_token_info.access_token
# [END retrieve_access_token]

_get_access_token()

```



## REST API调用模拟发送消息

```json
// Postman请求
POST https://fcm.googleapis.com/v1/projects/myproject-b5ae1/messages:send HTTP/1.1

Content-Type: application/json
Authorization: Bearer ya29.ElqKBGN2Ri_Uz...HnS_uNreA

{
   "message":{
      "token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
      "notification":{
        "body":"This is an FCM notification message!",
        "title":"FCM Message"
      }
     "data": {
     		"key": "value"
   		}
    //针对特定平台
  	"android":{
       "notification":{
         "icon":"stock_ticker_update",
         "color":"#7e55c3"
       }
     }
   }
}
```

```shell
curl -X POST -H "Authorization: Bearer ya29.ElqKBGN2Ri_Uz...HnS_uNreA" -H "Content-Type: application/json" -d '{
"message":{
   "notification":{
     "title":"FCM Message",
     "body":"This is an FCM Message"
   },
   "token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1..."
}}' https://fcm.googleapis.com/v1/projects/myproject-b5ae1/messages:send
```



## Firebase SDK初始化方式

[Firebase初始化原理](https://firebase.googleblog.com/2016/12/how-does-firebase-initialize-on-android.html)

多进程初始化问题：（Demo演示）Application调用Firebase

原因：新进程没有初始化Firebase SDK，闪退

解决方案1：自定义ContentProvider初始化Firebase

解决方案2：子进程如果不需要Firebase服务的时候，不要在Application中调用Firebase方法。

* --->首页进行初始化，产生问题：TV上CLEAR_TASK启动，每次启动都初始化不合理。
* 解决方案：添加flag判断是否已初始化SDK



## TV上遇到的问题

### TV没有通知栏，后台无法收到通知消息、通知+数据消息

通知流程：

```mermaid
graph TB

onStartCommand-->processIntent
processIntent-->handleIntentOnMainThread
handleIntentOnMainThread-->handleNotificationOpen{是否是打开通知栏Action}
handleNotificationOpen--是-->跳转Activity
handleNotificationOpen--否-->handleIntent
handleIntent-->action{Intent类型}
action--token刷新-->onNewToken
action--消息接收-->handleMessageIntent
handleMessageIntent-->alreadyReceivedMessage{是否接收过该消息}
alreadyReceivedMessage--是-->END((结束))
alreadyReceivedMessage--否-->passMessageIntentToSdk
passMessageIntentToSdk-->dispatchMessage
dispatchMessage-->判断是否是通知消息
判断是否是通知消息--是-->return
判断是否是通知消息--否-->onMessageReceived
```

* [查看源码](https://firebase.google.com/docs/libraries/)，发现判断条件在bundle参数中，可以修改Bundle参数。
* onStartCommand、onStart等声明为final，无法重写。
* handleIntent和handleIntentOnMainThread都可重写，保险写到handleIntentOnMainThread。

```kotlin
override fun handleIntentOnMainThread(intent: Intent?): Boolean {
	Log.e(TAG, "handleIntentOnMainThread before" + intent?.extras)
	//问题1: intent.extras拿到的是拷贝过的Bundle
  //需要直接修改intent的extras
	val key = Constants.MessageNotificationKeys.ENABLE_NOTIFICATION
	val oldKey = Constants.MessageNotificationKeys.ENABLE_NOTIFICATION.replace(
		Constants.MessageNotificationKeys.NOTIFICATION_PREFIX,
    Constants.MessageNotificationKeys.NOTIFICATION_PREFIX_OLD
	)
	intent?.removeExtra(key)
	intent?.removeExtra(oldKey)
	return super.handleIntentOnMainThread(intent)
}

override fun onMessageReceived(remoteMessage: RemoteMessage) {
	Log.d(TAG, "onMessageReceived, From: " + remoteMessage.from + remoteMessage.messageType)

	if (remoteMessage.data.isNotEmpty()) {
		Log.d(TAG, "onMessageReceived, Message data payload: " + remoteMessage.data)
	}
  //问题2: remoteMessage.notification为空，notification使用懒加载
  //需要重新包装回来.
  //1.重新把remove的key put进去再访问
  //2.手动构造notification。构造方法私有
  //3.不构造notification，只拿想要的值：title和body
	val intent = remoteMessage.toIntent()
	val key = Constants.MessageNotificationKeys.ENABLE_NOTIFICATION
	intent.putExtra(key, "1")
	RemoteMessage(intent.extras).notification?.let {
		Log.d(TAG, "onMessageReceived, Message Notification Body: " + remoteMessage.notification!!.body)
    Handler(Looper.getMainLooper()).post {
            Toast(this).apply {
                view = LayoutInflater.from(this@FCMTestService)
                    .inflate(R.layout.layout_notification, null, false)
                setGravity(Gravity.END or Gravity.BOTTOM, 30, 30)
                duration = Toast.LENGTH_LONG
            }.show()
        }
	}      
}
```



## 其他问题

* 模拟器有时候收不到通知，可以随便打开一下google应用，触发gms。怀疑是模拟器save state导致
* am force-stop 退出的应用，不会收到notification
* kill杀掉的进程，可以收到notification
* 后台弹窗问题：使用Toast，并且target sdk version小于26（弹窗问题小结）

gitbook介绍：[GitBook文档](https://chrisniael.gitbooks.io/gitbook-documentation/content/index.html)、[GitBook简明教程](http://www.chengweiyang.cn/gitbook/basic-usage/README.html)、gitbook插件：mermaid插件

