CKANClient-J
====

__A (very much) work in progress__

Provides an interface to a [CKAN 1.8](http://ckan.org) installation.

Methods currently implemented (some more thoroughly than others... at the moment):

__ACTIONS API__

* createDataset
* createGroup
* deleteDataset
* deleteGroup
* findDatasets
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
* getGroupList
* getGroupListAuthz
* getGroupPackages
* getGroupRevisions
* getLicenceList
* getPackageActivityList
* getResource
* getResourceStatus
* getRevision
* getRevisionList
* getRolesList
* getUser
* getUserActivityList
* getUserActivityListHTML
* getUserAutocomplete
* getUserFollowerCount
* getUserFollowerList
* getUserList

-------

Notes to self:

* Add factory/ies for results... and maybe for requests too?
* Add param objects or keep overloaded methods?
* Refactor code again once the API methods are complete
* Some people are prob not gonna like Allman braces/indenting in java

