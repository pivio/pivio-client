#!/bin/bash

# --- Option processing --------------------------------------------
display_usage() { 
    echo "Builds the docker container for the pivio-client" 
    echo -e "\nUsage:\n$0 [options]\n" 
    echo -e "  Options: -b           builds the pivio-client.jar artifact before building the docker container" 
    echo -e "           -h, --help   show the help" 
    echo -e "\nEG:\n  sudo $0 \n" 
} 

# check whether user had supplied -h or --help . If yes display usage 
if [[ ( ${@: -1} == "--help") ||  ${@: -1} == "-h" ]] 
then 
    display_usage
    exit 0
fi 

while getopts 'bh' flag; do
  case "${flag}" in
    b) BUILD_BEFORE=true ;;
    h) 
      display_usage
      exit 1
      ;;
    *) error "Unexpected option ${flag}" ;;
  esac
done

# -- some variables -----------------------------------------------
DIR_DOCKER=$(cd `dirname $0` && pwd)


# -- Body ---------------------------------------------------------

if [[ "${BUILD_BEFORE}" == "true" ]] 
then
    {
        echo "building..."
        pushd "${DIR_DOCKER}/.."
        ./gradlew build
        popd 
    }
fi

pushd "${DIR_DOCKER}"
docker build --pull --tag="leanix/pivio-client:latest" -f Dockerfile ../.
popd

echo "To run the npm license-checker execute the following command on the base directory of your npm project:"
echo "docker run --rm -v $(pwd):/source leanix/pivio-client npm-license-checker"

echo "Run the docker container with: docker run --rm leanix/pivio-client pivio"