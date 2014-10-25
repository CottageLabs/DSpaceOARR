package com.cottagelabs.oarr;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.dspace.app.util.Util;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dspace.core.ConfigurationManager;

public class RegistryFile
{
    public static void main(String[] args)
            throws Exception
    {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        options.addOption( "d", "dspace", true, "path to live dspace instance");
        options.addOption( "w", "webapps", true, "path to live webapps directory (e.g. in tomcat)");
        options.addOption("o", "output", true, "file to output to");

        CommandLine line = parser.parse(options, args);

        String dspaceLive = null;
        String webapps = null;
        String output = null;

        if (line.hasOption('d'))
        {
            dspaceLive = line.getOptionValue('d');
        }

        if (line.hasOption('w'))
        {
            webapps = line.getOptionValue('w');
        }

        if (line.hasOption('o'))
        {
            output = line.getOptionValue('o');
        }

        if (output == null || dspaceLive == null || webapps == null)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("RegistryFile", options);
            System.exit(0);
        }

        // now we have all the options, get the registry file contents as a json object
        JSONObject obj = RegistryFile.getRegistryFile(dspaceLive, webapps);

        // write the data out to file
        FileWriter fw = new FileWriter(new File(output));
        obj.writeJSONString(fw);
        fw.flush();
        fw.close();
    }

    public static JSONObject getRegistryFile(String dspaceLive, String webapps)
    {
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

        // REST
        boolean isRest = RegistryFile.isWebapp(webapps, "rest");
        if (isOai)
        {
            JSONObject rest = new JSONObject();
            rest.put("api_type", "dspace-rest");
            rest.put("base_url", baseUrl + "/rest");
            rest.put("version", Util.getSourceVersion());
            apis.add(rest);
        }

        // SWORDv1
        boolean isSword1 = RegistryFile.isWebapp(webapps, "sword");
        if (isSword1)
        {
            JSONObject sword = new JSONObject();
            sword.put("api_type", "sword");
            sword.put("base_url", baseUrl + "/sword/servicedocument");
            sword.put("version", "1.3");
            apis.add(sword);
            // FIXME: would be good to get the accepts/packaging options
        }

        // SWORDv2
        boolean isSword2 = RegistryFile.isWebapp(webapps, "swordv2");
        if (isSword2)
        {
            JSONObject sword = new JSONObject();
            sword.put("api_type", "sword");
            sword.put("base_url", baseUrl + "/swordv2/servicedocument");
            sword.put("version", "2.0");
            apis.add(sword);
            // FIXME: would be good to get the accepts/packaging options
        }

        return obj;
    }

    private static boolean isWebapp(String path, String webapp)
    {
        File wa = new File(path + File.separator + webapp);
        return (wa.exists() && wa.isDirectory());
    }
}
