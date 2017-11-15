#!/bin/bash
# ------------------------------------------------------------------
# Simple wrapper and help script used to start the pivio-client.
# ------------------------------------------------------------------


CONTAINER_NAME="leanix/pivio-client"


# --- Option processing --------------------------------------------
display_usage() { 
    echo "Shell script used to wrap the pivio client java call within the docker container." 
    echo -e "\nUsage:\ndocker run -it [docker run options] ${CONTAINER_NAME} pivio [pivio client options]\n" 
    echo -e "  pivio client options: -help        shows the origin pivio client help (default, if no option is provided)" 
    echo -e "                        -h, --help   show this help" 
    echo -e "\nEG:" 
    echo -e "  # Enter your project directory and use the pivio.yaml file in this directory" 
    echo -e "  docker run --rm -v \$(pwd):/source ${CONTAINER_NAME} pivio -serviceurl "https://local-eam.leanix.net/services/integrations/v2/pivio/document" -verbose \n"
    echo -e "  # or"     
    echo -e "  docker run --rm -v \$(pwd):/source --add-host local-eam.leanix.net:192.168.99.100 ${CONTAINER_NAME} pivio -serviceurl "https://local-eam.leanix.net/services/integrations/v2/pivio/document" -verbose -addfield \"api_token=vL6WOsHFczYDJPqsvs3ExxA6HY7jfSn9VNOKnnQe\"\n" 
} 

# check whether user had supplied -h or --help or no parameters . If yes display usage 
if [[ ( ${@: -1} == "--help") ||  ${@: -1} == "-h" || "$#" == 0 ]] 
then 
    display_usage
    exit 0
fi 

# --- body ---------------------------------------------------------

cd /source
java -jar /pivio.jar "$@"