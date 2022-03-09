# Summonerbuster

---

Hey you, thank you for being interested in how Summonerbuster works, I put a lot of hard work and love into this project because I thought it was a unique idea of something that has not been done before.

**Please note that this project is currently just the Backend as the Frontend is being redesigned**

I have tried to make it as simple as possible to host this project yourself, there is a lot todo before this can properly run following this you should be able to set it up in a few minutes.

---

###Software requirements

* git
* maven
* java 8
* postgres

---

###Working setup

*Hint: regardless of your setup, this should run on any system that can run the required software*

OS / Kernel : `Debian 11 5.10.0-9-amd64`

git : `git version 2.30.2`

maven : `Apache Maven 3.6.3`

java : `openjdk version "1.8.0_292"`

postgres :`psql (PostgreSQL) 13.5 (Debian 13.5-0+deb11u1)`

---

###Building the project

*this section assumes you are running a bash terminal*

```bash
git clone https://github.com/hawolt/summonerbuster
cd summonerbuster
bash build.sh
```

this script will take care of everything for you, the output of this script will save to `build.log` which you can look back at incase something goes wrong but it does not include any hints on how to fix errors.

---

###Setting up your database

the `resources` folder has a file called `database` which has all the three tables required for this project plus a few indexes for performance

---

###Running the project

when run successfully you will have `run-server.sh` and `run-parser.sh` which can be used to run the corresponding program.


when run without arguments both programs will print this little help message

```
Could not find required argument config
Command          | Shortcut  | Required  | Argument  | Once  | Description
config           | c         | true      | false     | true  | specify path to config
```

take a look at the `resources` folder, which includes `parser-config.json` and `server-config.json` please fill in the required data and run either the server or parser with the corresponding config file
e.g.  or 

---

#### Parser

`bash run-parser.sh --config resources/parser-config.json`

To properly run the parser without hitting rate-limits you will need 144 accounts per region in the configuration file

---

#### Server

`bash run-server.sh --config resources/server-config.json`

The server will expose the following endpoints

To query the database `localhost:${PORT}/v1/database/query`

To fetch the PUUID of the specified name `localhost:${PORT}/v1/client/puuid/{$NAME}`

To fetch profile information associated to the according PUUID `localhost:${PORT}/v1/client/profile/${PUUID}`

To fetch history information associated to the according PUUID `localhost:${PORT}/v1/client/history/${PUUID}`

To fetch Data required for a frontend `localhost:${PORT}/v1/data/dragon`

To fetch the total amount of queries made `localhost:${PORT}/v1/data/total`

To fetch information about the parsed games `localhost:${PORT}/v1/data/cache`
