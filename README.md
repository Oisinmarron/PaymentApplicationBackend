# How To Run
Run "PaymentAppApplication.class"

## Database
The database uses MySQL but runs using H2 so doesn't need any configuration. To check the database data - 
   1. Go to "http://localhost:8080/h2-console"
   2. Url = jdbc:h2:mem:testdb;MODE=MySQL
   3. username = sa
   4. password =
   5. Use the following commands to see the existing data -
      1. SELECT * FROM WEBHOOK;
      2. SELECT * FROM PAYMENT_DETAILS; 
      3. SELECT * FROM FAILED_WEBHOOK;

## Test APIs
If you would like to manually call the APIs through Postman or curl commands, I included the Postman collection that I used in the email - 
for the two APIs and to test a webhook.

I received a unique webhook url for testing from this location - https://webhook.site/. You can change the URL in the 
register webhook Postman request if needed.

## Tests
I didn't write tests for all methods - just a few to give examples.
The integration tests require Docker to be running in the background.