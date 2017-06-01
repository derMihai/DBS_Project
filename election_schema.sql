CREATE DATABASE election
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       CONNECTION LIMIT = -1;
GRANT CONNECT, TEMPORARY ON DATABASE election TO public;
GRANT ALL ON DATABASE election TO postgres;
GRANT ALL ON DATABASE election TO greuceanu;
GRANT ALL ON DATABASE election TO testuser;

CREATE SCHEMA public
  AUTHORIZATION postgres;

GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
COMMENT ON SCHEMA public
  IS 'standard public schema';
  
CREATE TABLE public."comesAlong"
(
  hname1 "char"[],
  hname2 "char"[],
  "pairOccurences" integer
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."comesAlong"
  OWNER TO testuser;
  
CREATE TABLE public.contains
(
  datum timestamp without time zone,
  pname character varying(25),
  hname character varying(140)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.contains
  OWNER TO testuser;
  
CREATE TABLE public.tweet
(
  pname character varying(25) NOT NULL,
  datum timestamp without time zone NOT NULL,
  retweets integer,
  likes integer,
  content character varying(200) NOT NULL,
  importance integer NOT NULL,
  retweet boolean,
  CONSTRAINT pk_tweets PRIMARY KEY (pname, datum)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.tweet
  OWNER TO testuser;
