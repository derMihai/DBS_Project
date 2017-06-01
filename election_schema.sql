
--CREATE SCHEMA dbs_schema1 
--
--	CREATE TABLE Tweet (PName varchar(25) NOT NULL,  
--			    Datum int NOT NULL,
--			    Zeit TIMESTAMP,
--			    Retweets int,
--			    Likes int,
--			    Retweet int,
--			    Content varchar(200) NOT NULL,
--			    Importance int NOT NULL,
--			    CONSTRAINT pk_Tweets PRIMARY KEY (PName,Datum)
--			    ) 
--			    
--	CREATE TABLE Contains (PName varchar(25),
--				Datum TIMESTAMP,
--				HName varchar(10),
--				CONSTRAINT fk_Contains PRIMARY KEY (PName,Datum,HName)
--				)
--				
--	CREATE TABLE Hashtag (HName varchar(10),
--				Occurences int,
--				CONSTRAINT pk_Hashtag PRIMARY KEY (HName)
--				) 
--	
--	CREATE TABLE ComesAlong (HName varchar(10),
--				HName2 varchar(10),
--				PairOccurence int,
--				CONSTRAINT fk_ComesAlong PRIMARY KEY (HName1,HName2)
--
--				) 
CREATE TABLE dbs_schema1."comesAlong"
(
  "hname1" "char"[],
  "hname2" "char"[],
  "pairOccurences" integer
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dbs_schema1."comesAlong"
  OWNER TO testuser;
  
  
CREATE TABLE dbs_schema1."contains"
(
  "pname" "char"[],
  "hname" "char"[],
  "datum" timestamp without time zone
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dbs_schema1."contains"
  OWNER TO testuser;
  
  
CREATE TABLE dbs_schema1.tweet
(
  pname character varying(25) NOT NULL,
  datum timestamp without time zone,
  retweets integer,
  likes integer,
  retweet integer,
  content character varying(200) NOT NULL,
  importance integer NOT NULL,
  CONSTRAINT pk_tweets PRIMARY KEY (pname, datum)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dbs_schema1.tweet
  OWNER TO testuser;  
  
    
  
  
  
  
  
  
  
  
  
  
  
