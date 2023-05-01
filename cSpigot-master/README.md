Setup
------------

Clone this repository

Init the Spigot-API and Spigot-Server submodules : `git submodule update --init`

Apply Patches : `./applyPatches.sh`

Compile the first time and add the project artifacts to the local maven cache: `mvn clean install`    
After that you can compile the api or server individually as well.    
Change into their folder first: `cd mspigot-api` or `cd mspigot-server`, then compile.     

For compiling test jars, `mvn clean package` should be used, as this does not install the compiled jars to the local maven cache, where other projects could automatically use them (if one is set up to do so)


### Create a new patch for server ###

`cd mspigot-server`

Do your changes.      
If there are new files, add them with `git add <file>`

Commit : `git commit -a -m <msg>`         
Note: Please use only single line messages here, and keep them simple. (Makes editing them easier).     

`cd ..`

Create Patch `./rebuildPatchesServer.sh`

### Edit an existing patch for server ###

`cd mspigot-server`

Find the commit hash of the patch you want to edit with: `git log`

Checkout the commit: `git checkout <commit hash>` (first 8 characters of the hash should be enough)    
Also note the exact commit message of the patch.

Reset commit, but keep its changes: `git reset HEAD^`

Do your changes. If there are new files, add them with `git add <file>`     
**Warning:** If the patch already contained new files, you must add those again as well.    
Then commit using the same commit message of the patch you had edited:
`git commit -a -m <exact commit message of patch>`

Note: If you use a different commit message here, the old patch file won't be overwritten but a second will be created, when rebuilding the patches, as the commit message is part of its file name.

`cd ..`

Rebuild the patches: `./rebuildPatchesServer.sh`

### Create or edit a patch for API ###

`cd mspigot-api`

Identical procedure as for the server. See above.

Rebuild the patches: `./rebuildPatchesAPI.sh`
# ValorSpigot
# ValorSpigot
