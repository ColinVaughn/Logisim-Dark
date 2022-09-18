package logisim_src.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mruniverse.slimelib.logs.SlimeLogs;


@SuppressWarnings({"deprecation", "unused"})
public class Updater {
    private static final String USER_AGENT = "Updater by Colin";
    // Direct download link
    private String downloadLink;
    // The folder where releases will be storage
    private final File releaseFolder;
    // The folder where builds will be storage
    private final File buildsFolder;
    // The plugin file
    private final File file;
    // ID of a project
    private final int id;
    // return a page
    private int page = 1;
    // Set the update type
    private final UpdateType updateType;
    // Get the outcome result
    private Result result = Result.SUCCESS;
    // If next page is empty set it to true, and get info from previous page.
    private boolean emptyPage;
    private String version;
    // Tag returned from GitHub
    private String tag;
    // Version of the plugin
    private final String currentVersion;
    // If true updater is going to log progress to the console.
    private boolean logger = true;
    // Updater thread
    private final Thread thread;

    private static final String DOWNLOAD = "/download";
    private static final String VERSIONS = "/versions";
    private static final String PAGE = "?page=";

    private static final String GITHUB_DOWNLOAD_LINK = "https://github.com/ColinVaughn/Logisim-Dark";

    private static final String GITHUB_API_RESOURCE = "https://api.github.com/repo/ColinVaughn/Logisim-Dark/releases/latest";

    public Updater(SlimeLogs logger, String currentVersion, int id, File file, UpdateType updateType) {
        this.logs = logger;
        this.currentVersion = currentVersion;
        // The folder where update will be downloaded
        File updateFolder = new File(file, "downloads");
        if (!updateFolder.exists()) {
            if (updateFolder.mkdirs()) logger.info("Downloads folder has been created");
        }
        this.releaseFolder = new File(updateFolder, "releases");
        if (!releaseFolder.exists()) {
            if (releaseFolder.mkdirs()) logger.info("Releases folder has been created");
        }
        this.buildsFolder = new File(updateFolder, "builds");
        if (!buildsFolder.exists()) {
            if (buildsFolder.mkdirs()) logger.info("Releases folder has been created");
        }
        this.id = id;
        this.file = file;
        this.updateType = updateType;

        thread = new Thread(new UpdaterRunnable());
        thread.start();
    }

    @SuppressWarnings("unused")
    public void disableLogs() {
        logger = false;
    }

    public enum UpdateType {
        // Checks only the version
        VERSION_CHECK,
        // Downloads without checking the version
        DOWNLOAD,
        // If updater finds new version automatically it downloads it.
        CHECK_DOWNLOAD

    }

    public enum Result {

        UPDATE_FOUND,

        NO_UPDATE,

        SUCCESS,

        FAILED,

        BAD_ID
    }

    /**
     * Get the result of the update.
     *
     * @return result of the update.
     * @see Result
     */
    public Result getResult() {
        waitThread();
        return result;
    }

    public String getVersion() {
        waitThread();
        return version;
    }

    /**
     * Check if id of resource is valid
     *
     * @param link link of the resource
     * @return true if id of resource is valid
     */
    private boolean checkResource(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            int code = connection.getResponseCode();

            if (code != 200) {
                connection.disconnect();
                result = Result.BAD_ID;
                return false;
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void checkUpdate(UpdateSite site) {
        switch (site) {
            case GITHUB:
                checkGithub();
                break;
        }
    }

    private void checkGithub() {
        try {
            URL url = new URL(GITHUB_API_RESOURCE);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader);
            JsonObject object = element.getAsJsonObject();
            element = object.get("tag_name");
            tag = element.toString().replaceAll("\"", "").replace("v", "");
            if (logger)
                logs.info("GITHUB |&e Checking for update...");
            if (shouldUpdate(tag, currentVersion) && updateType == UpdateType.VERSION_CHECK) {
                result = Result.UPDATE_FOUND;
                if (logger) {
                    logs.info("GITHUB |&6 Update found!");
                }
            } else if (updateType == UpdateType.DOWNLOAD) {
                if (logger)
                    logs.info("GITHUB |&e Trying to download update..");
                download(UpdateSite.GITHUB);
            } else if (updateType == UpdateType.CHECK_DOWNLOAD) {
                if (shouldUpdate(tag, currentVersion)) {
                    if (logger) {
                        logs.info("GITHUB |&e Update found, downloading now...");
                    }
                    download(UpdateSite.GITHUB);

                } else {
                    if (logger) {
                        logs.info("GITHUB |&6 You are using latest version of the plugin.");
                    }
                    result = Result.NO_UPDATE;
                }
            } else {
                if (logger) {
                    logs.info("GITHUB |&6 You are using latest version of the plugin.");
                }
                result = Result.NO_UPDATE;
            }
        } catch (Exception exception) {
            if (exception instanceof FileNotFoundException) {
                logs.info("&aNo updates are available on github");
                return;
            }
            logs.error("&cCan't find for updates on github :(");
            logs.error(exception);

        }
    }



    /**
     * Checks if plugin should be updated
     *
     * @param newVersion remote version
     * @param oldVersion current version
     */
    private boolean shouldUpdate(String newVersion, String oldVersion) {
        return !newVersion.equalsIgnoreCase(oldVersion);
    }

    /**
     * Downloads the file
     */
    private void download(UpdateSite site) {
        switch (site) {
            case GITHUB:
                downloadGithub();
                break;
        }
    }

    private void downloadGithub() {
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try {
            URL url = new URL(GITHUB_DOWNLOAD_LINK + tag + "/LogisimDark-" + tag + ".jar");
            in = new BufferedInputStream(url.openStream());

            fout = new FileOutputStream(new File(buildsFolder, file.getName() + "-v" + version + ".jar"));

            final byte[] data = new byte[4096];
            int count;
            while ((count = in.read(data, 0, 4096)) != -1) {
                fout.write(data, 0, count);
            }

            logs.info("&6Latest build from Github has been downloaded.");

        } catch (Exception ignored) {
            if (logger)
                logs.info("&cCan't download latest version automatically, download it manually from website.");
            logs.info(" ");
            result = Result.FAILED;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException exception) {
                logs.error("Can't download");
                logs.error(exception);
            }
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException exception) {
                logs.error("Can't download");
                logs.error(exception);
            }
        }
    }

    /**
     * Updater depends on thread's completion, so it is necessary to wait for thread to finish.
     */
    private void waitThread()
    {
        if (thread != null && thread.isAlive())
        {
            try
            {
                thread.join();
            } catch (InterruptedException exception) {
                logs.error("Can't download");
                logs.error(exception);
            }
        }
    }

    public enum UpdateSite {
        GITHUB,
    }

    public class UpdaterRunnable implements Runnable
    {

        public void run() {
            if (checkResource(downloadLink))
            {
                downloadLink = downloadLink + DOWNLOAD;
                checkUpdate(UpdateSite.GITHUB);
            }
        }
    }
}