# SRC
## Introduction
SRC, or Socially Responsible Computing is a set of principles that aim to guide programmers' decision making processes in tackling the ethical design decisions of their daily work. The goal of this project aims to allow CS 2400 students the opportunity to apply these principles in real-world problems. 

## Project Goals
With the help of The Lopez Urban Farm, we drafted and implemented a project proposal for a crop management system at the farm. The goal of this system to allow administration, volunteers, and the community to see what crops are currently in-season, in-stock, and harvested. 

## Project Design
As a team, we created an API endpoint that connects the user to a Google Sheets database through Java. The goal of this endpoint is to act as a client and backend for the database in such a way that users can input/modify/delete data, or use it to access the database. For example, the terminal can be used to modify the data locally, or the API can be used for a mobile app, webapp, or desktop application -- the possibilities are endless! Furthermore, this API can be modified to restrict user access through Google Cloud Computing IAM GSheets V4 service. This means that a given user can be given one of the following permissions: viewer, collaborator, admin, owner, or custom. This allows great flexibility in keeping the database under strict supervision, while allowing certain groups to get only the required access needed for their job.

## Conclusion
In all, by creating this crop management service, The Lopez Urban Farm is now able to manage their crops in whatever way they see fit!