# Management Library for Google Play Games
Allow simple removal of cheating players from leaderboards, using Google Play Games Management API.

## IMPORTANT
- DO NOT LEAVE THIS LIBRARY IN PRODUCTION. 
- It may take up to 72 hours (!!) once a player is 'hidden' until the player actually stop appearing in the leaderboard.
- The library must be used in an APK with the same bundle id (namespace) & signature as the production game. This is to allow Google Play sign-in. The easiest way is just to add the library to the game & open the MenuActivity.
- You must be signed-in within the game with the user of the Play Console.

## Usage
1. Project 'build.gradle': Add JitPack to the repositories -
```
allprojects {
    repositories {
        ...
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. App module 'build.gradle': Add games-management-lib dependency
```
dependencies {
    ...
    ....
    implementation 'com.github.sagishporer:games-management-lib:0.1.0'
}
```

3. App module, launch activity, onCreate() method: Open the library MenuActivity
```
@Override
public void onCreate(Bundle savedInstanceState) {
    ...            
    ...    

    Intent intent = new Intent(this, com.games.management.MenuActivity.class);   
    this.startActivity(intent);
}
```

## References & Resources
- Google Play Games Management API: https://developers.google.com/games/services/management
- Guide how to hide players using the Management API REST directly (this requires adding a new signed client to the Play Games Console which can not be removed later): https://gaute.app/dev-blog/gpgs-hide-player
