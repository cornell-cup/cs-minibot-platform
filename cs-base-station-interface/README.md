# cs-base-station-interface
Interface to represent any base station. This is not meant to be used standalone and should be used as a submodule within an implementing project. See: https://git-scm.com/docs/git-submodule
Related to this, the source contains an interfaces folder. These specifically should be refactored into their own repos and should not be considered reliable.

Confluence documentation for the base station can be found at https://confluence.cornell.edu/display/CCRT/Base+Station.

# Requirements
* GSON by Google - Necessary for serializing and deserializing JSON. Add this to your classpath. It can be found on Maven.
* Ice by ZeroC - Necessary for backwards compatibility communication with modbots. You can install ice natively in order to compile slice files to your native language; however, only the Java jar is needed to run the station. This will be added as a maven dependency in the future. See https://zeroc.com/
* Spark for Java - Hosts the HTTP server. Optional if the HTTP server is not being used. http://sparkjava.com/
