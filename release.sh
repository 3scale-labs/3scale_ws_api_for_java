#!/bin/sh
echo ""
echo "######################################"
echo "  Releasing 3scale Java Plugin"
echo "######################################"
echo ""

BRANCH=`git rev-parse --abbrev-ref HEAD`

echo "** Current Branch: $BRANCH **"
echo ""

RELEASE_VERSION=$1
DEV_VERSION=$2
GPG_PASSPHRASE=$3

if [ "x$RELEASE_VERSION" = "x" ]
then
  read -p "Release Version: " RELEASE_VERSION
fi

if [ "x$DEV_VERSION" = "x" ]
then
  read -p "New Development Version: " DEV_VERSION
fi

if [ "x$GPG_PASSPHRASE" = "x" ]
then
  read -p "GPG Passphrase: " GPG_PASSPHRASE
fi

echo "######################################"
echo "Release Version: $RELEASE_VERSION"
echo "Dev Version: $DEV_VERSION"
echo "######################################"

rm -rf ~/.m2/repository/io/3scale
mvn clean install
STATUS=$?
if [ $STATUS -eq 0 ]; then
  echo "Build success!"
else
  echo "Build failed!"
  exit 1
fi

mvn versions:set -DnewVersion=$RELEASE_VERSION
find . -name '*.versionsBackup' -exec rm -f {} \;
git add .
git commit -m "Prepare for release $RELEASE_VERSION"
git push origin $BRANCH

mvn clean install

git tag -a -m "Tagging release $RELEASE_VERSION" v$RELEASE_VERSION
git push origin v$RELEASE_VERSION

mvn clean deploy -Dgpg.passphrase=$GPG_PASSPHRASE

mvn versions:set -DnewVersion=$DEV_VERSION
find . -name '*.versionsBackup' -exec rm -f {} \;
git add .
git commit -m "Update to next development version: $DEV_VERSION"
git push origin $BRANCH
