package eu.neurovertex.tagtrack;


import com.tumblr.jumblr.types.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Neurovertex
 *         Date: 10/05/2014, 16:44
 */
public class RSSProducer {
	Document doc;
	Element channel;
	DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

	public RSSProducer() throws ParserConfigurationException, TransformerException {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element rss = doc.createElement("rss");
		rss.setAttribute("version", "2.0");
		doc.appendChild(rss);
		channel = doc.createElement("channel");
		rss.appendChild(channel);
		channel.appendChild(doc.createElement("title")).appendChild(doc.createTextNode("Tumblr Tag feed"));
		channel.appendChild(doc.createElement("description")).appendChild(doc.createTextNode("Tumblr Tag Tracker feed"));
		channel.appendChild(doc.createElement("generator")).appendChild(doc.createTextNode("Tumblr Tag Tracker"));
		channel.appendChild(doc.createElement("link")).appendChild(doc.createTextNode("http://tumblr.com"));
	}

	public void addItem(String tag, Post post) {
		Element item = doc.createElement("item");
		String[] text = getText(post);
		item.appendChild(doc.createElement("title")).appendChild(doc.createTextNode(String.format("#%s on %s's blog (%s) : %s", tag.replace(' ', '_'), post.getBlogName(), post.getType(), text[0])));
		item.appendChild(doc.createElement("description")).appendChild(doc.createTextNode(text[1]));
		item.appendChild(doc.createElement("link")).appendChild(doc.createTextNode(post.getPostUrl()));
		item.appendChild(doc.createElement("guid")).appendChild(doc.createTextNode(post.getPostUrl()));
		item.appendChild(doc.createElement("pubDate")).appendChild(doc.createTextNode(dateFormat.format(new Date(post.getTimestamp() * 1000))));
		for (String t : post.getTags())
			item.appendChild(doc.createElement("category")).appendChild(doc.createTextNode(t));
		channel.appendChild(item);
	}

	public static String[] getText(Post post) {
		String title, body;
		if (post instanceof TextPost) {
			body = ((TextPost) post).getBody();
			title = ((TextPost) post).getTitle();
			if (title == null)
				title = (body.length() < 100) ? body : body.substring(0, 96) + "...";
		} else if (post instanceof PhotoPost) {
			PhotoPost photo = (PhotoPost) post;
			title = (photo.getCaption() != null) ? photo.getCaption() : "[Photo]";
			StringBuilder sb = new StringBuilder();
			for (Photo p : photo.getPhotos())
				sb.append("<img src=\"").append(p.getOriginalSize().getUrl()).append("\"/><br />\n");
			sb.append(photo.getCaption());
			body = sb.toString();
		} else if (post instanceof QuotePost) {
			body = ((QuotePost) post).getText();
			title = (body.length() > 100) ? body.substring(0, 96) + "..." : body;
		} else if (post instanceof AnswerPost) {
			AnswerPost answer = (AnswerPost) post;
			title = "["+ answer.getAskingName() +"] "+ answer.getQuestion();
			body = answer.getAnswer();
		} else if (post instanceof LinkPost) {
			LinkPost link = (LinkPost) post;
			title = (link.getTitle() != null) ? link.getTitle() : "[" + link.getLinkUrl() + "]";
			body = "<a href=\"" + link.getLinkUrl() + "\">" + title + "</a><br />\n" + link.getDescription();
		} else if (post instanceof ChatPost) {
			ChatPost chat = (ChatPost) post;
			body = chat.getBody();
			title = chat.getTitle() != null ? chat.getTitle() : body.length() > 100 ? body.substring(0, 96) + "..." : body;
		} else if (post instanceof AudioPost) {
			AudioPost audio = (AudioPost) post;
			title = audio.getTrackName() != null ? audio.getTrackName() : audio.getSlug();
			body = audio.getCaption() != null ? audio.getCaption() : audio.getSlug();
		} else if (post instanceof VideoPost) {
			VideoPost video = (VideoPost) post;
			title = video.getSlug();
			body = "<img src=\""+ video.getThumbnailUrl() +"\"/><br />\n";
			if (video.getCaption() != null)
				body += video.getCaption();
		} else {
			title = '[' + post.getType() + ']';
			body = post.getSlug();
		}
		return new String[]{title, body};
	}

	public void write(PrintStream out) throws IOException, TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(out);

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);
	}

}
