# size-and-pack-service...

# Run in local with profile=local - SetUp Guide :

## **Prerequisite**

### Make sure you have access to :

* [Hashicorp/Vault](https://wmlink/hashicorp)
* [Ccm2](https://wmlink/ccm2)
* Dev DB
* https://dx.walmart.com/documents/product/Proximity/Maven-Usage-oe5f1pa4ew - Download Settings.xml and place it in /Users/<userid>/.m2/

## To run jar you need following you need following args/ vm options
    -Dspring.profiles.active=local
    -Dserver.port= Any port
    -DsqlServer.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
    -Dsql.username= get it from https://wmlink/hashicorp -> secret -> apparel-precision-kitt -> aex-size-and-pack -> dev -> sqlServer.username
    -Dsql.password= get it from https://wmlink/hashicorp -> secret -> apparel-precision-kitt -> aex-size-and-pack -> dev -> sqlServer.password
    -Dccm.configs.dir= path of CCM folder in your local  <From kitt.yml find version of CCM and download that version of CCM folder from : https://wmlink/ccm2  -> AEX_SIZE_AND_PACK -> <Vesrion mentoned in CCM> -> Download folder by clicking in work offline>
    -DmidasApi.authorization= fill content above-mentioned files with values mentioned in  : [hashicorp](https://wmlink/hashicorp) -> secret -> apparel-precision-kitt -> aex-strategy-service -> dev -> midasApi.authorization

## Usage of GCP Resources When Running Locally  
GCP resources (i.e. Cloud Storage) are used in this project, and requires the use of a credentials json file to authenticate.  When running the application locally which you'll need to provide.
1. Store the non-prod credentials file in a location of your choice (standard is /etc/secrets)
2. In your home directory, add the following export statement to your shell file (i.e. .zshrc, .bashrc)
    * `export GOOGLE_APPLICATION_CREDENTIALS=/path/to/credentials/credentials.json`
3. You can refresh your shell session using `source <.*rc> file`
4. If you're using Intellij or some IDE and running locally, you'll need to restart the application completely in order to pick up the changes
