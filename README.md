# Detector

Given component is resposible for verification of bank transactions.

## Registering

For app to run you need to register with the bank with the following CURL command

**DO NOT FORGET TO CHANGE NAME IN BODY**

`curl -X POST 'http://ec2-16-16-217-235.eu-north-1.compute.amazonaws.com:8080/detectors' -H 'Content-Type: application/json' --data-raw '{"name": "YOUR TEAM NAME HERE"}'`

As a response you will get token. Put that token into src/main/java/resources/application.properties

You should be set to go. 

## General Rules

Detector asks for a set of unverified transactions from bank.
For each transaction, detector validates following 
* Sender is the owner of the sender account
* Sender has no warrants issued.
* Sender has a valid contract.

Once detector validates the transaction, it should tell the bank if the transactions is valid or not.
Bank tracks which transactions it has given to detector and tracks if detector takes too long to validate.
Bank will never issue an outdated transaction. 
