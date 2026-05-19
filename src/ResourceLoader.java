import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

public final class ResourceLoader implements GuiLayout.ResourceResolver
{
    private final Map<String, ImageIcon> imagesById;

    private ResourceLoader(Map<String, ImageIcon> imagesById)
    {
        this.imagesById = Collections.unmodifiableMap(imagesById);
    }

    public static ResourceLoader load(File resourcesXml) throws Exception
    {
        return new ResourceLoader(loadFromXml(resourcesXml));
    }

    @Override
    public ImageIcon resolve(String resourceId)
    {
        ImageIcon image = imagesById.get(resourceId);
        if(image == null)
        {
            throw new IllegalArgumentException("Unknown resource id: " + resourceId);
        }
        return image;
    }

    public ImageIcon tryResolve(String resourceId)
    {
        if(resourceId == null)
        {
            return null;
        }
        return imagesById.get(resourceId);
    }

    private static Map<String, ImageIcon> loadFromXml(File xmlFile) throws Exception
    {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        Element root = doc.getDocumentElement();
        if(!"resources".equalsIgnoreCase(root.getTagName()))
        {
            throw new IllegalArgumentException("Resource file root must be <resources>");
        }
        Map<String, ImageIcon> map = new HashMap<>();
        NodeList children = root.getChildNodes();
        for(int i = 0; i < children.getLength(); i++)
        {
            Node n = children.item(i);
            if(n.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }
            Element el = (Element) n;
            String tag = el.getTagName();
            if("Image".equals(tag))
            {
                String id = el.getAttribute("id").trim();
                String path = el.getAttribute("path").trim();
                if(id.isEmpty() || path.isEmpty())
                {
                    throw new IllegalArgumentException("Each <Image> needs id and path");
                }
                map.put(id, loadImage(path));
            }
        }
        return map;
    }

    private static ImageIcon loadImage(String path)
    {
        try
        {
            BufferedImage image = ImageIO.read(new File(path));
            if(image == null)
            {
                throw new IOException("ImageIO.read returned null for: " + path);
            }
            return new ImageIcon(image);
        }
        catch(IOException e)
        {
            throw new IllegalArgumentException("Could not load resource file: " + path, e);
        }
    }
}
