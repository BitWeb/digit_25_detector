# Detector

Given component is resposible for verification of bank transactions.

* Github: [https://github.com/BitWeb/digit_25_detector](https://github.com/BitWeb/digit_25_detector)
* Dashboard: [https://dash.sandbox.bitw3b.eu/public-dashboards/0d1829f98adf4945bb5cec11a4e57a23](https://dash.sandbox.bitw3b.eu/public-dashboards/0d1829f98adf4945bb5cec11a4e57a23)

## Registering

For app to run you need to register. You need a team name and a fork of the detector repository.
Once you have  a team and a fork, you can register with the following CURL command:

**DO NOT FORGET TO CHANGE NAME AND GITHUB REPOSITORY URL IN BODY**

`curl -X POST 'https://digit.sandbox.bitw3b.eu/detectors' -H 'Content-Type: application/json' --data-raw '{"name": "<TEAM NAME>", "githubUrl": "<GITHUB FORK URL>"}'`

As a response you will get token. Put that token into src/main/java/resources/application.properties

You should be set to go. If you have issues, please feel free to ask for help. 

## Running

* Running in IDE is best option
* Running in terminal `./gradlew bootRun --args='--detector.token=<your_token>'`

## Service limitations

* Each api token is limited to 50 concurrent requests.
* Each api token is limited to 10000 pending transactions.

## Evaluation

At 07.05.2025 21:00 evaluators will start going through registered teams and executing applications in an isolated environment.
Evaluators will run a single instance of application and let it run for 3 minutes to get a baseline. 

***Testing machine:***
* 2 vCPU 
* 8 GB ram


## Post event notes

With a small delay, none the less, here is the Bitweb's version of the optimized detector app. [Check the branch out here.](https://github.com/BitWeb/digit_25_detector/tree/opt_one)

You can use the banking server to test locally, the code is in given [Repository](https://github.com/BitWeb/digit_25_bank)

PS: Special shoutout to Silver Abel for providing a very good implementation. Feel free to check out his [Fork](https://github.com/silverabel/digit_25_detector)
