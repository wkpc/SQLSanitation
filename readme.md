# Information
By Weikai Chen \
Email: weikai.c@ucalgary.ca \
Github: https://github.com/wkpc/SQLSanitation

This program tests the implementation of SQL, input sanitation, password hashing, and encryption in
a java environment with a bare-bones GUI.

## What it does
When launched, a simple GUI displays two things: On the left is a login screen, and on the right is an
encrypted database. To login, choose the input sanitation method of your choice, and enter a valid set of
login credentials. You can try using SQL injections to gain access, or simply use the default username 
and password of "admin" and "password", respectively.

Once you have signed in, you can now interact with the database. By default, the right side of the GUI
displays the contents of the database, in AES-256 encryption. After signing in, you can select the 
"Decrypt Data" button to view the decrypted version instead.

## How to run it
Coming soon

## How it works
### database.db:
All the data for this project (i.e. login credentials and database contents) are stored in separate SQL
tables within the "database.db" file. This file is not provided with the download, and is instead created
the first time the program is launched. Once created, two tables "hashed" (for login credentials) and 
"encrypted" (for database contents) are added to the file, along with some sample starter data. "hashed"
is initially populated with only one entry, the default username and password of "admin" and "password".
The password is first hashed before being stored in the SQL table, while the username remains as is. The
"encrypted" table is initially populated with the word "hello", after being encrypted with the AES-256 
algorithm.

### passwords.jks:
When the program is first launched, a check is made for a "passwords.jks" file. This is where the keys for
encryption/decryption are stored. If no "passwords.jks" file is found, then a new key is generated and 
used to encrypt the contents of the "encrypted" table. A new "passwords.jks" file is then created and the
newly generated key is stored within. If a "passwords.jks" file *was* found, then no new key is created 
and instead the key in the "passwords.jks" is used.

### Login:
When the user presses the login button, a SQL query is sent to the database, looking for all entries in
the table with matching usernames AND passwords.

**As a side note, it is impossible to perform SQL injections on the password field of the login, since
password input is hashed before being passed to the database.**

The "unsanitized" method uses Statements to formulate the SQL command, thus leaving it open to SQL 
injections. One possible injection here is typing "admin' OR '" into the username field. This modifies
the SQL query is from \
"SELECT * FROM hashed WHERE user = 'username' AND password = 'password';" \
to: \
"SELECT * FROM hashed WHERE user = 'admin' OR '' AND password = 'password';" \
Since the "AND" operator takes priority over "OR" when deciding order of operations, the need for a 
matching username and password is bypassed to only require a matching username.

The "sanitized" method uses Java's built-in sanitation using PreparedStatements to formulate SQL queries,
preventing the use of SQL injections.

The "custom" method is my attempt at creating a simple input sanitation algorithm. This uses "()" 
surrounding each of the conditions within the query to prevent users from circumventing any part of the 
login conditions (i.e. only needing a username match or only needing a password match) by locking the 
order of operations. This means to successfully pull off a false positive, one would need to inject SQL
commands into both the username and password fields, which shouldn't be possible due to the hashing of 
password input. A custom string escaper is also used, to escape special characters such as ', ", 
and \.

The methods used in the "custom" method are not exclusive, and could be combined with the "sanitized" 
method for added security in future implementations.

### Encryption:
Coming soon