# ParseProxyObject-Kotlin

Class [ParseHelper](https://github.com/BytePace/ParseProxyObject-Kotlin/blob/master/app/src/main/java/com/bytepace/parseproxyobject/ParseHelper.kt) wraps ParseObject so it can be passed to new intent or bundle as serializable extra.
## How to use it?
Everything is very simple. Follow next steps.
### Step 1
You have any ParseObject subclass. You must pass it into ParseHelper.create() method.
```kotlin
val user = ParseUser()
user.put(KEY_LOCATION, ParseGeoPoint(0.121, 0.4454)
// Any additional data for ParseObject
val proxyUser = ParseHelper.create(user)
```
### Step 2
Take an object and pass it into Intent or Bundle class instance.
<br><br>
Intent:
```kotlin
val intent = Intent(context, MainActivity::class.java)
intent.putExtra(KEY_USER, proxyUser)
```
Bundle:
```kotlin
val arguments = Bundle()
intent.putSerializable(KEY_USER, proxyUser)
```
### Step 3
Get an object from Intent or Bundle. Then call the ```restore()``` method.
<br><br>
Intent:
```kotlin
val proxyUser = intent.extras[KEY_USER] as ParseProxyObject
val user = proxyUser.restore<ParseUser>() 
```
Bundle:
```kotlin
val proxyUser = arguments?.getSerializable(KEY_USER) as ParseProxyObject
val user = proxyUser.restore<ParseUser>() 
```

## Note
1) Custom class (e.g. ParseGeoPoint) can be added for convertation to Serializable object
2) Return value of ```isDirty()``` method will ```true``` after ```restore()``` invocation