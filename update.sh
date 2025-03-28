#!/bin/bash

rsync -a -v -i --exclude-from='ignore.txt' /home/james/Projects/Commons/Secure-Commons/ /home/james/Projects/Commons/Secure-Commons-master/
read -p "Press enter to continue..."

# TODO: make this file work locally to update my local repo
# no need to install things every time

# cd metascoop
# Make it run tidy to check for dependency changes
# echo "::group::Running tidy"
# go mod tidy
# echo "::endgroup::"
# echo "::group::Building metascoop executable"
# go build -o metascoop
# echo "::endgroup::"

# we no longer need the GH_ACCESS_TOKEN so we can remove it
# ./metascoop -ap=../apps.yaml -rd=../fdroid/repo -pat="$GH_ACCESS_TOKEN" $1
# EXIT_CODE=$?
# cd ..

# echo "Scoop had an exit code of $EXIT_CODE"

# set -e

# if [ $EXIT_CODE -eq 2 ]; then
    # Exit code 2 means that there were no significant changes
#    echo "This means that there were no significant changes"
#    exit 0
# elif [ $EXIT_CODE -eq 0 ]; then
    # Exit code 0 means that we can commit everything & push

#    echo "This means that we now have changes we should push"

#    git config --global user.name 'github-actions'
#    git config --global user.email '41898282+github-actions[bot]@users.noreply.github.com'

#    git add .
#    git commit -m"Automated update"
#    git push
# else 
#    echo "This is an unexpected error"

#    exit $EXIT_CODE
# fi
