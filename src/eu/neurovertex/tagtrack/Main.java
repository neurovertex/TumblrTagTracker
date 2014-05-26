package eu.neurovertex.tagtrack;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jeremie
 * Date: 02/05/2014
 * Time: 20:06
 * @version 0.1 build 1405262107
 */
public class Main {
	public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException {
		// Read in the JSON data for the credentials

		Options cliOpts = new Options();
		cliOpts.addOption("h", "help", false, "Display help");
		cliOpts.addOption("n", "dry-run", false, "Doesn't run the tracker. Combine with -s to adjust settings on dry runs.");
		cliOpts.addOption("s", "save-settings", false, "Save settings after including command-line parameters.");
		cliOpts.addOption("c", "clear-settings", false, "Doesn't load the settings from the file");
		cliOpts.addOption("o", "output", true, "Output file, default is - (= console output)");
		cliOpts.addOption("version", true, "Display version, implies -n");

		cliOpts.addOption("ck", "consumer-key", true, "The consumer key");
		cliOpts.addOption("cs", "consumer-secret", true, "The consumer secret");
		cliOpts.addOption("ot", "oauth-token", true, "The OAuth token");
		cliOpts.addOption("os", "oauth-secret", true, "The OAuth token secret");

		//noinspection AccessStaticViaInstance
		cliOpts.addOption(OptionBuilder.withArgName("tags").withValueSeparator(',').withLongOpt("tags").withDescription("Comma-separated list of tags to track").hasArg().create("t"));
		cliOpts.addOption("a", "append-tags", false, "Adds tags from the --tags parameters from those loaded from the config file instead of replacing them");

		try {
			CommandLine cli = new BasicParser().parse(cliOpts, args);
			Settings settings = parseCLI(cli);
			if (!cli.hasOption("n") && !cli.hasOption("h")) {
				if ((settings.containsKey("consumer_key") && settings.containsKey("consumer_secret") || settings.containsKey("oauth_token") && settings.containsKey("oauth_token_secret"))) {

					JumblrClient client;

					if (settings.containsKey("consumer_key") && settings.containsKey("consumer_secret"))
						client = new JumblrClient(settings.get("consumer_key"), settings.get("consumer_secret"));
					else
						client = new JumblrClient();

					if (settings.containsKey("oauth_token") && settings.containsKey("oauth_token_secret"))
						client.setToken(settings.get("oauth_token"), settings.get("oauth_token_secret"));

					RSSProducer rss = new RSSProducer();
					Map<String, String> options = new HashMap<>();
					options.put("filter", "text");

					Map<Long, Map.Entry<String, Post>> posts = new TreeMap<>(Collections.reverseOrder()); // Avoiding duplicates in case a post has multiple tracked tags

					for (String tag : settings.get("tags").split(",")) {
						List<Post> postList = client.tagged(tag, options);
						for (Post post : postList)
							posts.put(post.getId(), new AbstractMap.SimpleEntry<>(tag, post));
					}

					for (Map.Entry<String,Post> entry : posts.values())
						rss.addItem(entry.getKey(), entry.getValue());

					PrintStream out = settings.get("output").equals("-") ? System.out : new PrintStream(settings.get("output"));
					rss.write(out);
				} else {
					System.err.println("Needs an API key or an oauth token to opereate");
				}
			}
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar TumblrTagTracker.jar", "\n", cliOpts, "", true);
			System.exit(e.getMessage().equals("help") ? 0 : -1);
		}


	}

	public static Settings parseCLI(CommandLine cli) throws IOException, ParseException {
		if (cli.hasOption("help"))
			throw new ParseException("help");

		if (cli.hasOption("version")) {
			System.out.println("Version : 0.1 build 1405262107");
		}

		String[] args = cli.getArgs();
		String configFile = "config.json";
		if (args.length > 1)
			throw new ParseException("Too many arguments");
		else if (args.length == 1)
			configFile = args[0];

		Settings settings = new Settings(configFile);

		if (!cli.hasOption("c"))
			settings.load();

		if (cli.hasOption("ck"))
			settings.put("consumer_key", cli.getOptionValue("ck"));
		if (cli.hasOption("cs"))
			settings.put("consumer_secret", cli.getOptionValue("cs"));

		if (cli.hasOption("ot"))
			settings.put("oauth_token", cli.getOptionValue("ot"));
		if (cli.hasOption("os"))
			settings.put("oauth_token_secret", cli.getOptionValue("os"));

		if (cli.hasOption("o"))
			settings.put("output", cli.getOptionValue("o"));
		else if (!settings.containsKey("output"))
			settings.put("output", "-");

		if (cli.hasOption("t")) {
			String[] tags = cli.getOptionValues("t");
			if (cli.hasOption("a") && settings.containsKey("tags")) {
				tags = ArrayUtils.addAll(tags, settings.get("tags").split(","));
			}
			settings.put("tags", StringUtils.join(tags, ","));
		}

		if (cli.hasOption("s"))
			settings.save();

		return settings;
	}
}
