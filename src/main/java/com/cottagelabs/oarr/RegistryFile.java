package com.cottagelabs.oarr;

import org.dspace.app.util.Util;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dspace.core.ConfigurationManager;

public class RegistryFile
{
    public static void main(String[] args)
    {
        String dspaceLive = "/srv/duo/duo-upgrade";
        String webapps = "/srv/duo/duo-upgrade/webapps"; // note this is not the tomcat directory, for the purposes of testing only
        String dspaceCfg = dspaceLive + File.separator + "config" + File.separator + "dspace.cfg";

        ConfigurationManager.loadConfig(dspaceCfg);
        String baseUrl = ConfigurationManager.getProperty("dspace.baseUrl");

        JSONObject obj = new JSONObject();

        // last updated date (i.e. now!)
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss'Z'");
        String formattedDate = sdf.format(now);
        obj.put("last_updated", formattedDate);

        // the main bits of the register
        JSONObject register = new JSONObject();
        obj.put("register", register);

        JSONArray metadataList = new JSONArray();
        JSONObject metadata = new JSONObject();
        metadataList.add(metadata);
        register.put("metadata", metadataList);

        JSONObject record = new JSONObject();
        metadata.put("record", record);

        // repository name
        String name = ConfigurationManager.getProperty("dspace.name");
        record.put("name", name);

        // metadata language
        String defaultLang = ConfigurationManager.getProperty("default.language");
        metadata.put("lang", defaultLang);
        metadata.put("default", true);

        // repository url
        String dspaceUrl = ConfigurationManager.getProperty("dspace.url");
        record.put("url", dspaceUrl);

        // software record
        JSONArray softwareList = new JSONArray();
        JSONObject software = new JSONObject();
        softwareList.add(software);
        register.put("software", softwareList);

        software.put("name", "DSpace");
        software.put("url", "http://dspace.org");
        software.put("version", Util.getSourceVersion());

        // contact record
        JSONArray contactList = new JSONArray();
        register.put("contact", contactList);

        String feedbackEmail = ConfigurationManager.getProperty("feedback.recipient");
        JSONObject feedback = new JSONObject();
        JSONArray roleList = new JSONArray();
        roleList.add("feedback");
        feedback.put("role", roleList);
        JSONObject feedbackDetails = new JSONObject();
        feedbackDetails.put("email", feedbackEmail);
        feedback.put("details", feedbackDetails);
        contactList.add(feedback);

        String adminEmail = ConfigurationManager.getProperty("mail.admin");
        JSONObject admin = new JSONObject();
        JSONArray aRoleList = new JSONArray();
        aRoleList.add("admin");
        admin.put("role", aRoleList);
        JSONObject adminDetails = new JSONObject();
        adminDetails.put("email", adminEmail);
        admin.put("details", adminDetails);
        contactList.add(admin);

        // available apis (we know all of them so do them one by one)
        JSONArray apis = new JSONArray();
        register.put("api", apis);

        // LNI
        boolean isLni = RegistryFile.isWebapp(webapps, "lni");
        if (isLni)
        {
            JSONObject lni = new JSONObject();
            lni.put("api_type", "lni");
            lni.put("base_url", baseUrl + "/lni");
            lni.put("version", Util.getSourceVersion());
            apis.add(lni);
        }

        // OAI
        boolean isOai = RegistryFile.isWebapp(webapps, "oai");
        if (isOai)
        {
            JSONObject oai = new JSONObject();
            oai.put("api_type", "oai-pmh");
            oai.put("base_url", baseUrl + "/oai/request");
            oai.put("version", "2.0");
            apis.add(oai);
            // FIXME: would be good to get the metadata formats, but that turns out to be quite hard
        }

        System.out.print(obj);
    }

    private static boolean isWebapp(String path, String webapp)
    {
        File wa = new File(path + File.separator + webapp);
        return (wa.exists() && wa.isDirectory());
    }
}
