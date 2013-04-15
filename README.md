CKANClient-J
====

A (very much) work in progress
------------------------------

Provides an interface to a [CKAN 1.8+](http://ckan.org) installation.

Methods currently implemented (some more thoroughly than others... at the moment):

Dependencies
------------

I'm currently building against (but other versions may work):

* [Commons logging 1.0.4](http://archive.apache.org/dist/commons/logging/binaries/)
* [Gson 2.2.2](https://code.google.com/p/google-gson/downloads/list?can=1)
* [HttpClient 4.2.3](http://hc.apache.org/downloads.cgi)
* [HttpCore 4.2.2](http://hc.apache.org/downloads.cgi)

ACTIONS API
-----------

(WIP = Work In Progress)

* createDataset
* createGroup
* createMember
* deleteDataset
* deleteGroup
* --findDatasets-- -> replaced by searchDatasets
* getActivityDetailList
* getAmFollowingDataset
* getAmFollowingUser
* getCurrentPackageListWithResources
* getDashboardActivityList
* getDashboardActivityListHTML
* getDataset
* getDatasetFolloweeCount
* getDatasetFolloweeList
* getDatasetFollowerCount
* getDatasetFollowerList
* getFormatAutoComplete
* getGroup
* getGroupActivityList
* getGroupActivityListHTML
* getGroupList - WIP
* getGroupListAuthz
* getGroupPackages
* getGroupRevisions
* getLicenceList
* getMemberList - WIP
* getPackageActivityList
* getPackageRelationships - WIP
* getPackageRevisions
* getResource
* getResourceStatus - WIP
* getRevision
* getRevisionList - WIP
* getRolesList - WIP
* getUser
* getUserActivityList
* getUserActivityListHTML
* getUserAutocomplete
* getUserFollowerCount
* getUserFollowerList
* getUserList
* searchDatasets - WIP


-------

Notes to self:

* Add factory/ies for results... and maybe for requests too?
* Add param objects or keep overloaded methods?
* Refactor code again once the API methods are complete
* Some people are prob not gonna like Allman braces/indenting in java

