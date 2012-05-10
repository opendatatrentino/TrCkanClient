package org.ckan;


import com.google.gson.Gson;


/**
 * The primary interface to this package the Client class is responsible
 * for managing all interactions with a given connection.
 *
 * @author      Ross Jones <ross.jones@okfn.org>
 * @version     1.7
 * @since       2012-05-01
 */
public final class Client {

    public class Response {
        public String success;
    }

    private Connection _connection = null;

    public Client( Connection c, String apikey ) {
        this._connection = c;
        this._connection.setApiKey(apikey);
    }

    protected <T> T LoadClass( Class<T> cls, String data ) {
        Gson gson = new Gson();
        return gson.fromJson(data, cls);
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
    public Dataset getDatasetByName(String name)
            throws CKANException {
        String returned_json = this._connection.Post("/api/action/package_show",
                                                     "{\"id\":\"" + name + "\"}" );
        Dataset.Response r = LoadClass( Dataset.Response.class, returned_json);
        if ( ! r.success ) {

        }
        return r.result;
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
    public Group getGroupByName(String name)
            throws CKANException {
        String returned_json = this._connection.Post("/api/action/group_show",
                                                     "{\"id\":\"" + name + "\"}" );
        Group.Response r = LoadClass( Group.Response.class, returned_json);
        if ( ! r.success ) {

        }
        return r.result;
    }

}






