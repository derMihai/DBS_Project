CREATE DATABASE election
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       CONNECTION LIMIT = -1;
GRANT CONNECT, TEMPORARY ON DATABASE election TO public;
GRANT ALL ON DATABASE election TO postgres;
GRANT ALL ON DATABASE election TO testuser;

CREATE SCHEMA public
  AUTHORIZATION postgres;

GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
COMMENT ON SCHEMA public
  IS 'standard public schema';
  
CREATE TABLE public.comesalong
(
  hname1 character varying(140) NOT NULL,
  hname2 character varying(140) NOT NULL,
  pairoccurences integer,
  CONSTRAINT "primKey" PRIMARY KEY (hname1, hname2)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.comesalong
  OWNER TO testuser;
  
CREATE TABLE public.contains
(
  datum timestamp without time zone NOT NULL,
  pname character varying(25) NOT NULL,
  hname character varying(140) NOT NULL,
  CONSTRAINT "primKey2" PRIMARY KEY (datum, pname, hname)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.contains
  OWNER TO testuser;

CREATE TABLE public.hashtag
(
  hname character varying(140) NOT NULL,
  importance double precision,
  cluster integer,
  CONSTRAINT hashtagname PRIMARY KEY (hname)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.hashtag
  OWNER TO testuser;
  
CREATE TABLE public.tweet
(
  pname character varying(25) NOT NULL,
  datum timestamp without time zone NOT NULL,
  retweets integer,
  likes integer,
  content character varying(200) NOT NULL,
  importance double precision NOT NULL,
  retweet boolean,
  CONSTRAINT pk_tweets PRIMARY KEY (pname, datum)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.tweet
  OWNER TO testuser;
