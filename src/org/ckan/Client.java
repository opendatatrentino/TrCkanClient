/*
JCKANClient - Data Catalogue Software client in Java
Copyright (C) 2013 Newcastle University
Copyright (C) 2012 Open Knowledge Foundation

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.ckan;

import org.ckan.resource.impl.Dataset;
import org.ckan.resource.impl.Group;
import com.google.gson.Gson;

import java.util.Map;
import java.util.HashMap;
import org.ckan.resource.impl.Resource;
import org.ckan.resource.impl.Revision;
import org.ckan.resource.impl.User;
import org.ckan.result.CKANResult;
import org.ckan.result.list.impl.DatasetList;
import org.ckan.result.impl.BooleanResult;
import org.ckan.result.impl.IntegerResult;
import org.ckan.result.impl.StringResult;
import org.ckan.result.impl.DatasetResult;
import org.ckan.result.impl.GroupResult;
import org.ckan.result.impl.DatasetSearchResult;
import org.ckan.result.impl.ResourceResult;
import org.ckan.result.list.impl.DatasetSearchList;
import org.ckan.result.impl.RevisionResult;
import org.ckan.result.impl.UserResult;
import org.ckan.result.list.impl.ActivityList;
import org.ckan.result.list.impl.LicenceList;
import org.ckan.result.list.impl.RevisionList;
import org.ckan.result.list.impl.StringList;
import org.ckan.result.list.impl.UserList;

/**
 * The primary interface to this package the Client class is responsible
 * for managing all interactions with a given connection.
 *
 * @author      Andrew Martin <andrew.martin@ncl.ac.uk>, Ross Jones <ross.jones@okfn.org>
 * @version     1.8
 * @since       2013-02-18
 */
public final class Client
{
    protected Connection connection = null;
    protected Gson gson;

    /**
    * Constructs a new Client for making requests to a remote CKAN instance.
    *
    * @param  c A Connection object containing info on the location of the
    *         CKAN Instance.
    * @param  apikey A user's API Key sent with every request.
    */
    public Client(Connection c, String apikey)
    {
        this.connection = c;
        this.connection.setApiKey(apikey);
        this.gson = new Gson();
    }

    /**
    * Loads a JSON string into a class of the specified type.
    */
    protected <T> T getGsonObjectFromJson(Class<T> cls, String data, String action) throws CKANException
    {
        Object o = gson.fromJson(data, cls);
        handleError((CKANResult)o,data,action);
        return (T)o;
    }

    protected String getJsonFromGsonObject(Object o)
    {
        return gson.toJson(o);
    }
    
    /**
    * Handles error responses from CKAN
    *
    * When given a JSON string it will generate a valid CKANException
    * containing all of the error messages from the JSON.
    *
    * @param  json The JSON response
    * @param  action The name of the action calling this for the primary
    *         error message.
    * @throws A CKANException containing the error messages contained in the
    *         provided JSON.
    */
    protected void handleError(String json, String action) throws CKANException
    {
        CKANException exception = new CKANException("Error at: Client."+action+"()");
        HashMap hm  = gson.fromJson(json,HashMap.class);
        Map<String,Object> m = (Map<String,Object>)hm.get("error");
        for (Map.Entry<String,Object> entry : m.entrySet())
        {
            if (!entry.getKey().startsWith("_"))
            {
                exception.addError(entry.getValue()+" - "+entry.getKey());
            }
        }
        throw exception;
    }

    protected void handleError(CKANResult result, String json, String action) throws CKANException
    {
        if(!result.success)
        {
            handleError(json, action);
        }
    }
    
    protected String postAndReturnTheJSON(String uri, String jsonParams) throws CKANException
    {
        return postAndReturnTheJSON(uri,jsonParams,false);
    }
    
    protected String postAndReturnTheJSON(String uri, String jsonParams, boolean debug) throws CKANException
    {
        String json = this.connection.post(uri,jsonParams);
        if(debug)
        {
            System.out.println(json);
        }
        return json;
    }

    protected <T> T getAGsonResult(Class<T> cls, String uri, String jsonParams, String action, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(cls,postAndReturnTheJSON(uri,jsonParams,debug),action);
    }
    
    protected DatasetList getADatasetList(String uri, String jsonParams, String action, boolean debug) throws CKANException
    {
        return getAGsonResult(DatasetList.class,uri,jsonParams,action,debug);
    }

    protected UserList getAUserList(String uri, String jsonParams, String action, boolean debug) throws CKANException
    {
        return getAGsonResult(UserList.class,uri,jsonParams,action,debug);
    }
    
    public Gson getGsonObject()
    {
        return this.gson;
    }
    
    /*********************************************************************************************/
    
    
    /**
    * Creates a dataset on the server
    *
    * Takes the provided dataset and sends it to the server to
    * perform an create, and then returns the newly created dataset.
    *
    * @param  dataset A dataset instance
    * @returns The Dataset as it now exists
    * @throws A CKANException if the request fails
    */
    public Dataset createDataset(Dataset dataset) throws CKANException
    {
        return createDataset(dataset,false);
    }

    public Dataset createDataset(Dataset dataset, boolean debug) throws CKANException
    {
        DatasetResult dr = getGsonObjectFromJson(DatasetResult.class,postAndReturnTheJSON("/api/action/package_create",getJsonFromGsonObject(dataset),debug),"createDataset");
        return dr.result;
    }

    /**
    * Creates a Group on the server
    *
    * Takes the provided Group and sends it to the server to
    * perform an create, and then returns the newly created Group.
    *
    * @param  group A Group instance
    * @returns The Group as it now exists on the server
    * @throws A CKANException if the request fails
    */
    public Group createGroup(Group group) throws CKANException
    {
        return createGroup(group,false);
    }

    public Group createGroup(Group group, boolean debug) throws CKANException
    {
        GroupResult r = getGsonObjectFromJson(GroupResult.class,postAndReturnTheJSON("/api/action/package_create",getJsonFromGsonObject(group)),"createGroup");
        return r.result;
    }

    /**
    * Deletes a dataset
    *
    * Deletes the dataset specified with the provided name/id
    *
    * @param  name The name or ID of the dataset to delete
    * @throws A CKANException if the request fails
    */
    public void deleteDataset(String name) throws CKANException
    {
        deleteDataset(name,false);
    }

    public void deleteDataset(String name, boolean debug) throws CKANException
    {
        getGsonObjectFromJson(DatasetResult.class,postAndReturnTheJSON("/api/action/package_delete","{\"id\":\""+name+"\"}",debug),"deleteDataset");
    }

    /**
    * Deletes a Group
    *
    * Deletes the group specified with the provided name/id
    *
    * @param  name The name or ID of the group to delete
    * @throws A CKANException if the request fails
    */
    public void deleteGroup(String name) throws CKANException
    {
        deleteGroup(name,false);
    }

    public void deleteGroup(String name, boolean debug) throws CKANException
    {
        getGsonObjectFromJson(GroupResult.class,postAndReturnTheJSON("/api/action/group_delete","{\"id\":\""+name+"\"}",debug),"deleteGroup");
    }

    /**
    * Uses the provided search term to find datasets on the server
    *
    * Takes the provided query and locates those datasets that match the query
    *
    * @param  query The search terms
    * @returns A SearchResults object that contains a count and the objects
    * @throws A CKANException if the request fails
    */
    public DatasetSearchList findDatasets(String query) throws CKANException
    {
        return findDatasets(query,false);
    }
    
    public DatasetSearchList findDatasets(String query, boolean debug) throws CKANException
    {
        DatasetSearchResult sr = getGsonObjectFromJson(DatasetSearchResult.class,postAndReturnTheJSON("/api/action/package_search","{\"q\":\""+query+"\"}",debug),"findDatasets");
        return sr.result;
    }

    /********************/

    public ActivityList getActivityDetailList(String id) throws CKANException
    {
        return getActivityDetailList(id,false);
    }

    public ActivityList getActivityDetailList(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(ActivityList.class,postAndReturnTheJSON("/api/action/activity_detail_list","{\"id\":\""+id+"\"}",debug),"getActivityDetailList");
    }

    /********************/
    
    public BooleanResult getAmFollowingDataset(String id) throws CKANException
    {
        return getAmFollowingDataset(id,false);
    }

    public BooleanResult getAmFollowingDataset(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(BooleanResult.class,postAndReturnTheJSON("/api/action/am_following_dataset","{\"id\":\""+id+"\"}",debug),"amFollowingDataset");
    }

    /********************/
    
    public BooleanResult getAmFollowingUser(String id) throws CKANException
    {
        return getAmFollowingUser(id,false);
    }
    
    public BooleanResult getAmFollowingUser(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(BooleanResult.class,postAndReturnTheJSON("/api/action/am_following_user","{\"id\":\""+id+"\"}",debug),"amFollowingUser");
    }

    /********************/

    public DatasetList getCurrentPackageListWithResources(int limit, int page) throws CKANException
    {
        return getCurrentPackageListWithResources(limit,page,false);
    }
    
    public DatasetList getCurrentPackageListWithResources(int limit, int page, boolean debug) throws CKANException
    {
        return getADatasetList("/api/action/current_package_list_with_resources","{\"limit\":\""+limit+"\",\"page\":\""+page+"\"}","getCurrentPackageListWithResources",debug);
    }
    
    /********************/

    public ActivityList getDashboardActivityList(String id) throws CKANException
    {
        return getDashboardActivityList(id,false);
    }

    public ActivityList getDashboardActivityList(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(ActivityList.class,postAndReturnTheJSON("/api/action/dashboard_activity_list","{\"id\":\""+id+"\"}",debug),"getDashboardActivityList");
    }

    /********************/

    public StringResult getDashboardActivityListHTML(String id) throws CKANException
    {
        return getDashboardActivityListHTML(id,false);
    }

    public StringResult getDashboardActivityListHTML(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(StringResult.class,postAndReturnTheJSON("/api/action/dashboard_activity_list_html","{\"id\":\""+id+"\"}",debug),"getDashboardActivityListHTML");
    }

    /********************/

    public IntegerResult getDatasetFolloweeCount(String id) throws CKANException
    {
        return getDatasetFolloweeCount(id,false);
    }

    public IntegerResult getDatasetFolloweeCount(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(IntegerResult.class,postAndReturnTheJSON("/api/action/dataset_followee_count","{\"id\":\""+id+"\"}",debug),"getDatasetFolloweeCount");
    }

    /********************/

    public DatasetList getDatasetFolloweeList(String id) throws CKANException
    {
        return getDatasetFolloweeList(id,false);
    }

    public DatasetList getDatasetFolloweeList(String id, boolean debug) throws CKANException
    {
        return getADatasetList("/api/action/dataset_followee_list","{\"id\":\""+id+"\"}","getDatasetFolloweeList",debug);
    }

    /********************/

    public IntegerResult getDatasetFollowerCount(String id) throws CKANException
    {
        return getDatasetFollowerCount(id,false);
    }

    public IntegerResult getDatasetFollowerCount(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(IntegerResult.class,postAndReturnTheJSON("/api/action/dataset_follower_count","{\"id\":\""+id+"\"}",debug),"getDatasetFollowerCount");
    }

    /********************/

    public DatasetList getDatasetFollowerList(String id) throws CKANException
    {
        return getDatasetFollowerList(id,false);
    }

    public DatasetList getDatasetFollowerList(String id, boolean debug) throws CKANException
    {
        return getADatasetList("/api/action/dataset_follower_list","{\"id\":\""+id+"\"}","getDatasetFollowerList",debug);
    }

    /**
    * Retrieves a dataset
    *
    * Retrieves the dataset with the given name, or ID, from the CKAN
    * connection specified in the Client constructor.
    *
    * @param  name The name or ID of the dataset to fetch
    * @returns The Dataset for the provided name.
    * @throws A CKANException if the request fails
    */
    public Dataset getDataset(String name) throws CKANException
    {
        return getDataset(name, false);
    }

    public Dataset getDataset(String name, boolean debug) throws CKANException
    {
        DatasetResult dr = getGsonObjectFromJson(DatasetResult.class,postAndReturnTheJSON("/api/action/package_show","{\"id\":\""+name+"\"}",debug),"getDataset");
        return dr.result;
    }

    /********************/

    public StringList getFormatAutocomplete(String query, int limit) throws CKANException
    {
        return getFormatAutocomplete(query,limit,false);
    }

    public StringList getFormatAutocomplete(String query, int limit, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(StringList.class,postAndReturnTheJSON("/api/action/format_autocomplete","{\"q\":\""+query+"\",\"limit\":\""+limit+"\"}",debug),"getFormatAutocomplete");
    }

    /**
    * Retrieves a group
    *
    * Retrieves the group with the given name, or ID, from the CKAN
    * connection specified in the Client constructor.
    *
    * @param  name The name or ID of the group to fetch
    * @returns The Group instance for the provided name.
    * @throws A CKANException if the request fails
    */
    public Group getGroup(String id) throws CKANException
    {
        return getGroup(id,false);
    }

    public Group getGroup(String id, boolean debug) throws CKANException
    {
        GroupResult r = getGsonObjectFromJson(GroupResult.class,postAndReturnTheJSON("/api/action/group_show","{\"id\":\""+id+"\"}",debug),"getGroup");
        return r.result;
    }

    /********************/

    public ActivityList getGroupActivityList(String id) throws CKANException
    {
        return getGroupActivityList(id,false);
    }

    public ActivityList getGroupActivityList(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(ActivityList.class,postAndReturnTheJSON("/api/action/group_activity_list","{\"id\":\""+id+"\"}",debug),"getGroupActivityList");
    }

    /********************/

    public StringResult getGroupActivityListHTML(String id) throws CKANException
    {
        return getGroupActivityListHTML(id,false);
    }

    public StringResult getGroupActivityListHTML(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(StringResult.class,postAndReturnTheJSON("/api/action/group_activity_list_html","{\"id\":\""+id+"\"}",debug),"getGroupActivityList");
    }

    /*******************/ /** WIP **/

    public StringList getGroupList() throws CKANException
    {
        return getGroupList(false);
    }

    public StringList getGroupList(boolean debug) throws CKANException
    {
        /*
         * OPT DEPR : order_by <- don't include?
         * OPT : sort - name/packages
         * OPT : sort order - asc/"desc?"
         * OPT : groups - ["group1","group2"]
         * OPT : all_fields - full group instead of just names (i.e. verbosity)
         */
        
        return getGsonObjectFromJson(StringList.class,postAndReturnTheJSON("/api/action/group_list","{\"groups\":[\"test-group\"]}",debug),"getGroupList");
    }

    /*******************/

    public StringList getGroupListAuthz(boolean availableOnly) throws CKANException
    {
        return getGroupListAuthz(availableOnly, false);
    }

    public StringList getGroupListAuthz(boolean availableOnly, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(StringList.class,postAndReturnTheJSON("/api/action/group_list_authz","{\"available_only\":\""+availableOnly+"\"}",debug),"getGroupListAuthz");
    }

    /*******************/

    public DatasetList getGroupPackages(String id, int limit) throws CKANException
    {
        return getGroupPackages(id,limit,false);
    }

    public DatasetList getGroupPackages(String id, int limit, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(DatasetList.class,postAndReturnTheJSON("/api/action/group_package_show","{\"id\":\""+id+"\",\"limit\":\""+limit+"\"}",debug),"getGroupPackages");
    }

    /*******************/

    public RevisionList getGroupRevisions(String id) throws CKANException
    {
        return getGroupRevisions(id,false);
    }

    public RevisionList getGroupRevisions(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(RevisionList.class,postAndReturnTheJSON("/api/action/group_revision_list","{\"id\":\""+id+"\"}",debug),"getGroupRevisions");
    }

    /********************/

    public LicenceList getLicenceList() throws CKANException
    {
        return getLicenceList(false);
    }

    public LicenceList getLicenceList(boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(LicenceList.class,postAndReturnTheJSON("/api/action/licence_list","{}",debug),"getLicenceList");
    }

    /********************/

    public ActivityList getPackageActivityList(String id) throws CKANException
    {
        return getPackageActivityList(id,false);
    }

    public ActivityList getPackageActivityList(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(ActivityList.class,postAndReturnTheJSON("/api/action/package_activity_list","{\"id\":\""+id+"\"}",debug),"getPackageActivityList");
    }

    /********************/

    public Resource getResource(String id) throws CKANException
    {
        return getResource(id,false);
    }

    public Resource getResource(String id, boolean debug) throws CKANException
    {
        ResourceResult rr = getGsonObjectFromJson(ResourceResult.class,postAndReturnTheJSON("/api/action/resource_show","{\"id\":\""+id+"\"}",debug),"getResource");
        return rr.result;
    }

    /********************/ /** WIP **/

    public void getResourceStatus(String id) throws CKANException
    {
        /*return*/ getResourceStatus(id,false);
    }

    public void getResourceStatus(String id, boolean debug) throws CKANException
    {
        getGsonObjectFromJson(ResourceResult.class,postAndReturnTheJSON("/api/action/resource_status_show","{\"id\":\""+id+"\"}",debug),"getResourceStatus");
        /*return rr.result;*/
    }

    /********************/ /** Does this need a site id as a param??? **/

    public StringList getRevisionList() throws CKANException
    {
        return getRevisionList(false);
    }

    public StringList getRevisionList(boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(StringList.class,postAndReturnTheJSON("/api/action/revision_list","{\"q\":\"\"}",debug),"getRevisionList");
    }

    /********************/

    public Revision getRevision(String id) throws CKANException
    {
        return getRevision(id,false);
    }

    public Revision getRevision(String id, boolean debug) throws CKANException
    {
        RevisionResult rr = getGsonObjectFromJson(RevisionResult.class,postAndReturnTheJSON("/api/action/revision_show","{\"id\":\""+id+"\"}",debug),"getRevision");
        return rr.result;
    }

    /********************/ /** WIP **/

    public void getRolesList(String domainObject, String user, String authorizationGroup) throws CKANException
    {
        /*return*/ getRolesList(domainObject, user, authorizationGroup, false);
    }

    public void getRolesList(String domainObject, String user, String authorizationGroup, boolean debug) throws CKANException
    {
        /*return*/ getGsonObjectFromJson(StringList.class,postAndReturnTheJSON("/api/action/roles_show","{\"domain_object\":\""+domainObject+"\",\"user\":\""+user+"\",\"authorization_group\":\""+authorizationGroup+"\"}",debug),"getRolesList");
    }

    /********************/

    public ActivityList getUserActivityList(String id) throws CKANException
    {
        return getUserActivityList(id,false);
    }

    public ActivityList getUserActivityList(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(ActivityList.class,postAndReturnTheJSON("/api/action/user_activity_list","{\"id\":\""+id+"\"}",debug),"getUserActivityList");
    }

    /********************/

    public StringResult getUserActivityListHTML(String id) throws CKANException
    {
        return getUserActivityListHTML(id,false);
    }

    public StringResult getUserActivityListHTML(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(StringResult.class,postAndReturnTheJSON("/api/action/user_activity_list_html","{\"id\":\""+id+"\"}",debug),"getUserActivityListHTML");
    }
    
    /********************/

    public UserList getUserAutocomplete(String query, int limit) throws CKANException
    {
        return getUserAutocomplete(query,limit,false);
    }

    public UserList getUserAutocomplete(String query, int limit, boolean debug) throws CKANException
    {
        return getAUserList("/api/action/user_autocomplete","{\"q\":\""+query+"\",\"limit\":\""+limit+"\"}","getUserAutocomplete",debug);
    }

    /********************/

    public IntegerResult getUserFollowerCount(String id) throws CKANException
    {
        return getUserFollowerCount(id,false);
    }

    public IntegerResult getUserFollowerCount(String id, boolean debug) throws CKANException
    {
        return getGsonObjectFromJson(IntegerResult.class,postAndReturnTheJSON("/api/action/user_follower_count","{\"id\":\""+id+"\"}",debug),"getUserFollowerCount");
    }

    /********************/

    public UserList getUserFollowerList(String id) throws CKANException
    {
        return getUserFollowerList(id,false);
    }

    public UserList getUserFollowerList(String id, boolean debug) throws CKANException
    {
        return getAUserList("/api/action/user_follower_list","{\"id\":\""+id+"\"}","getUserFollowerList",debug);
    }

    /********************/

    public UserList getUserList(String query, User.OrderBy orderBy) throws CKANException
    {
        return getUserList(query,orderBy,false);
    }

    public UserList getUserList(String query, User.OrderBy orderBy, boolean debug) throws CKANException
    {
        return getAUserList("/api/action/user_list","{\"q\":\""+query+"\",\"order_by\":\""+orderBy+"\"}","getUserList",debug);
    }
    
    /********************/

    public User getUser(String id) throws CKANException
    {
        return getUser(id,false);
    }

    public User getUser(String id, boolean debug) throws CKANException
    {
        UserResult ur = getGsonObjectFromJson(UserResult.class,postAndReturnTheJSON("/api/action/user_show","{\"id\":\""+id+"\"}",debug),"getUser");
        return ur.result;
    }

    public User getUser(User user) throws CKANException
    {
        return getUser(user,false);
    }

    public User getUser(User user, boolean debug) throws CKANException
    {
        String uid = user.getId();
        String name = user.getName();
        /* If uid is not blank use it, failing that use the name,
         * failing that just send a blank string
         */
        String id = uid!=null&&!uid.equals("")?uid:name!=null&&!name.equals("")?name:"";
        return getUser(id,debug);
    }
}






