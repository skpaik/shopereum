# Flare
Emergency Messenger App

## Problem statement
In an emergency or disaster scenario, power outages and damaged infrastructure makes communication through mobile phones difficult, and in some cases impossible.

## Positioning Strategy
The emergency messenger app will be used to promote Right Mesh, alongside the other items in our portfolio of apps in a new Play Store account for RightMesh (not Left). It will be used as a consumer facing demo of the RightMesh platform.

This is a real way to help people - if we get it right, we could save lives. 

## Use cases / user stories
- As an office worker in a downtown high rise, I carry a phone all the time and generally always have a data or internet connection. However, I’ve been told the “big one” [earthquake] is due, and I just want to be able to keep safe when it happens. It would be chaos downtown, especially without internet. Can you imagine? 
- As a person with some first aid training, I always hope I never have to use it, but there is a reason I took it in the first place - I want to be prepared. I would help anyone else I could if something were to happen though. 
- As a mother with a teenager in a school nearby, I want to keep tabs on my daughter. This means knowing where she is and how she is. She’s just 13 and I want to respect her freedom, but with all those shootings and things nowadays you can never be too careful. 

## Required Documents
- App Name: Flare
- Type: Emergency Messenger App
- CodeBase: https://code.leftofthedot.com/rightmeshapps/flare
- Slide: https://goo.gl/9WB9Kp
- BD Team Feedback: https://goo.gl/uYTija
- High Level Doc: https://goo.gl/avznsz
- Design: https://invis.io/KQM12139738WJB
- UML: https://goo.gl/LtHCWF
- DB design: http://dbdesigner.net/designer

## UML
```
Sequence Diagram
```
![Check Diagram](https://code.leftofthedot.com/rightmeshapps/flare/raw/development/app/keystore/UML/Sequence_Diagram_FLare_1.jpg)


```
App current Folder Structure 
```
![Check Diagram](https://code.leftofthedot.com/rightmeshapps/flare/raw/development/app/keystore/UML/Folder_Structure.png)



# Android Boilerplate
We follow this [Android App](https://code.leftofthedot.com/anjan/mvp-architecture-app) for every Android project structure.
So, for better understanding, please go through structure and [README.md](https://code.leftofthedot.com/anjan/mvp-architecture-app/blob/master/Android-Project-Guideline_v1.1.pdf) file.


## - How to get started to develop
Please go through our code guideline.

## - How to make a new module or feature
1. Create a new package under `ui` called `signin`
2. Create an new Activity called `SignInActivity`. You could also use a Fragment.
3. Define the view interface that your Activity is going to implement. Create a new interface called `SignInMvpView` that extends `MvpView`. Add the methods that you think will be necessary, e.g. `showSignInSuccessful()`
4. Create a `SignInPresenter` class that extends `BasePresenter<SignInMvpView>`
5. Implement the methods in `SignInPresenter` that your Activity requires to perform the necessary actions, e.g. `signIn(String email)`. Once the sign in action finishes you should call `getMvpView().showSignInSuccessful()`.
6. Make your  `SignInActivity` implement `SignInMvpView` and implement the required methods like `showSignInSuccessful()`
7. In your activity, always extend `BaseActivity<V extends MvpView, P extends BasePresenter<V>>` that contains the MvpView and Presenter. Don't forget to implement required methods on Activity .Also, set up a click listener in your button that calls `presenter.signIn(email)`.
8. Create a `SignInPresenterTest`and write unit tests for `signIn(email)`. Remember to mock the  `SignInMvpView` and also the `DataManager`.

## - How to test
1. Open App
2. Login with username and add profile picture.
3. Set mood to **Master** or **Client** according to your need.
4. Wait for  user discovery in **In Range** tab.
5. Click on user to start conversation.
6. Click on user avatar for details of user.
7. From right top menu, you can access different menu.