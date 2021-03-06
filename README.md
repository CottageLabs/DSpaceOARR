# DSpace OARR Registry File Generator

## Build

Check the code out, and then locally run

    mvn clean package
    mvn dependency:copy-dependencies

This will give you the compiled version of this library in

    target/dspace-oarr-1.0-SNAPSHOT.jar

And all of the dependencies of this library in

    target/dependency

NOTE: there will be a lot of dependencies, because this code depends on DSpace, which has a lot of dependencies of its own

## Install

To install and be able to run this in the context of DSpace, just copy the following files into [dspace-live]/lib:

    target/dspace-oarr-1.0-SNAPSHOT.jar
    target/dependency/json-simple-1.1.jar

## Usage

For convenience you can use the DSpace class runner to run this code, if you have installed it as above.

    [dspace-live]/bin/dspace dsrun com.cottagelabs.oarr.RegistryFile -d [path to live DSpace] -w [path to tomcat webapps] -o [path to output file to create]

This will analyse your DSpace installation, and write the resulting RegistryFile (see below) into the output file specified.

## The Registry File

The Registry File we need to generate is of the following format:

```python
{
    "last_updated" : "# datestamp of last registry file modification #",

    "register" : {
        "replaces" : "# oarr info uri of repository this one replaces (info:oarr:[identifier]) #",
        "operational_status" : "Trial | Operational",

        "metadata" : [
            {
                "lang" : "en",
                "default" : true|false,
                "record" : {
                    "country_code" : "# two-letter iso code for country #",
                    "twitter" : "# repository's twitter handle #",
                    "acronym" : "# repository name acronym #",
                    "description" : "# free text description of repository #",
                    "established_date" : "# date established #",
                    "language_code" : [# languages of content found in repo (iso-639-1) #],
                    "name" : "# name of repository #",
                    "url" : "# url for repository home page #",
                    "subject" : ["# list of subject classification terms for the repository #"],
                    "repository_type" : ["# list of vocabulary terms for the repository #"],
                    "certification" : ["# list of certifications held by this repository #"],
                    "content_type" : ["# list of vocabulary terms for the content in this repository #"]
                }
            }
        ],
        "software" : [
            {
                "name" : "# name of software used to provide this repository #",
                "version" : "# version of software used to provide this repository #",
                "url" : "# url for the software/this version of the software #"
            }
        ],
        "contact" : [
            {
                "role" : ["# contact role with regard to this repository #"],
                "details": {
                    "name" : "# contact name #",
                    "email" : "# contact email #",
                    "address" : "# postal address for contact #",
                    "fax": "# fax number of contact #",
                    "phone": "# phone number of contact #",
                    "lat" : "# latitude of contact location #",
                    "lon" : "# longitude of contact location #",
                    "job_title" : "# contact job title #"
                }
            }
        ],
        "organisation" : [
            {
                "role" : [# organisation roles with regard to this repository #],
                "details" : {
                    "name" : "# name of organisation #",
                    "acronym" : "# acronym of organisation #",
                    "url" : "# organisation url #",

                    "unit" : "# name of organisation's unit responsible #"
                    "unit_acronym" : "# acronym of unit responsible #",
                    "unit_url" : "# url of responsible unit #",

                    "country_code" : "# two letter country code organisation resides in #",
                    "lat" : "# latitude of organisation/unit #",
                    "lon" : "# longitude of organisation/unit #"
                }
            }
        ],
        "policy" : [
            {
                "policy_type" : "# vocabulary term for policy type #",
                "description" : "# description of policy terms, human readable #",
                "terms" : ["# list of vocabulary terms describing the policy #"]
            }
        ],
        "api" : [
            {
                "api_type" : "# api type from known list or free text #",
                "version" : "# version of the API #",
                "base_url" : "# base url of API #",

                "metadata_formats" : [{"prefix" : "# prefix #", "namespace" : "# namespace #", "schema" : "# schema#"}],
                "accepts" : [# list of accepted mimetypes #],
                "accept_packaging" : [# list of accepted package formats #]
            }
        ],
        "integration": [
            {
                "integrated_with" : "# type of system integrated with #",
                "nature" : "# nature of integration #",
                "url" : "# url of system integrated with, if available #",
                "software" : "# name of software integrated with #",
                "version": "# version of software integrated with #"
            }
        ]
    }
}
```