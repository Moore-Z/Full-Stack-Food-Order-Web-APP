# Full-Stack-Food-Order-Web-APP

Using Spring Boot2 Framework build one full stack food order web app with backend operation pages that allow the employers to manage employees, dishes, dish categories, and orders from backend pages using data from the MySQL database with the help of MyBatis API. Using the JASN data form transferring input data between human operator and backend database. Using Spring Session to ensure correct data format and consistent data input and Thread to avoid redundant RESTful return data. Used the insertFill to autofill some redundant data which is used to sort services.



After clicking the icons on the web page, the VUE framework will send AJAX commands by JSON or regular requests to call backend services operating by various service controllers. Filter was introduced to block all non-log-in users from directly accessing the operation page. DTO is introduced to transfer more nested data between different tables, like the meal table containing dish ids from the dish table.



The user interface was represented by HTML5 to display adjustable page size and log in to their account by receiving the verification code on their phone and the system can also track the user thread in the shopping cart service. Users could browse all available dishes or meals and check out their shopping cart.
