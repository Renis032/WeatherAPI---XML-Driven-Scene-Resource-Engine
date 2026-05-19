import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

public final class GuiLayout
{
    @FunctionalInterface
    public interface ResourceResolver
    {
        ImageIcon resolve(String resourceId);
    }

    private final Map<String, JComponent> byId = new HashMap<>();
    private final ResourceResolver resources;

    public GuiLayout(ResourceResolver resources)
    {
        this.resources = resources;
    }

    public void loadFromXml(JFrame frame, File xmlFile) throws Exception
    {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        Element root = doc.getDocumentElement();
        if(!"layout".equalsIgnoreCase(root.getTagName()))
        {
            throw new IllegalArgumentException("Root element must be <layout>");
        }

        NodeList children = root.getChildNodes();
        for(int i = 0; i < children.getLength(); i++)
        {
            Node n = children.item(i);
            if(n.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }
            Element el = (Element) n;
            JComponent c = createComponent(el);
            if(c == null)
            {
                continue;
            }
            String id = el.getAttribute("id").trim();
            if(!id.isEmpty())
            {
                byId.put(id, c);
            }
            frame.add(c);
        }
    }

    public <T extends JComponent> T get(String id, Class<T> type)
    {
        JComponent c = byId.get(id);
        if(c == null)
        {
            throw new IllegalArgumentException("No component with id: " + id);
        }
        if(!type.isInstance(c))
        {
            throw new ClassCastException("Component " + id + " is " + c.getClass().getName() + ", not " + type.getName());
        }
        return (T) c;
    }

    private JComponent createComponent(Element el)
    {
        String tag = el.getTagName();
        switch(tag)
        {
            case "JTextField":
                return buildTextField(el);
            case "JLabel":
                return buildLabel(el);
            case "JButton":
                return buildButton(el);
            default:
                throw new IllegalArgumentException("Unknown element: " + tag);
        }
    }

    private JTextField buildTextField(Element el)
    {
        JTextField tf = new JTextField();
        applyBounds(tf, el);
        applyFont(tf, el);
        return tf;
    }

    private JLabel buildLabel(Element el)
    {
        JLabel label;
        String rid = readRssRef(el);
        if(!rid.isEmpty())
        {
            label = new JLabel(resources.resolve(rid));
        }
        else
        {
            label = new JLabel();
        }

        String textAttr = el.getAttribute("text");
        if(!textAttr.isEmpty())
        {
            label.setText(textAttr);
        }

        applyBounds(label, el);
        applyFont(label, el);

        String ha = el.getAttribute("horizontalAlignment").trim().toLowerCase(Locale.ROOT);
        if(!ha.isEmpty())
        {
            label.setHorizontalAlignment(parseHorizontalAlignment(ha));
        }
        return label;
    }

    private JButton buildButton(Element el)
    {
        String rid = readRssRef(el);
        JButton button = rid.isEmpty() ? new JButton() : new JButton(resources.resolve(rid));
        applyBounds(button, el);

        String cursor = el.getAttribute("cursor").trim().toLowerCase(Locale.ROOT);
        if("hand".equals(cursor))
        {
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        return button;
    }

    private static String readRssRef(Element el)
    {
        String id = el.getAttribute("rss").trim();
        if(!id.isEmpty())
        {
            return id;
        }

        //TODO: throw exception
        return "";
    }

    private static void applyBounds(JComponent c, Element el)
    {
        c.setBounds(parseBoundsFromElement(el));
    }

    private static Rectangle parseBoundsFromElement(Element el)
    {
        String bound = el.getAttribute("bound").trim();
        String position = el.getAttribute("position").trim();
        String size = el.getAttribute("size").trim();

        if(!bound.isEmpty())
        {
            return parseQuad(bound);
        }
        if(!position.isEmpty() && !size.isEmpty())
        {
            int[] xy = parsePair(position, "position");
            int[] wh = parsePair(size, "size");
            return new Rectangle(xy[0], xy[1], wh[0], wh[1]);
        }
        throw new IllegalArgumentException("Provide either bound=\"x,y,w,h\"");
    }

    private static Rectangle parseQuad(String raw)
    {
        String[] parts = stripBracesSplit(raw);
        if(parts.length != 4)
        {
            throw new IllegalArgumentException("attribute must have four numbers: x, y, w, h: " + raw);
        }
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        int w = Integer.parseInt(parts[2].trim());
        int h = Integer.parseInt(parts[3].trim());
        return new Rectangle(x, y, w, h);
    }

    private static int[] parsePair(String raw, String name)
    {
        String[] parts = stripBracesSplit(raw);
        if(parts.length != 2)
        {
            throw new IllegalArgumentException(name + " must have two numbers; got: " + raw);
        }
        return new int[] {
            Integer.parseInt(parts[0].trim()),
            Integer.parseInt(parts[1].trim())
        };
    }

    private static String[] stripBracesSplit(String raw)
    {
        if(raw == null || raw.trim().isEmpty())
        {
            throw new IllegalArgumentException("Empty value");
        }
        String s = raw.trim();
        if(s.startsWith("{"))
        {
            s = s.substring(1);
        }
        if(s.endsWith("}"))
        {
            s = s.substring(0, s.length() - 1);
        }
        return s.split(",");
    }

    private static void applyFont(JComponent c, Element el)
    {
        String family = el.getAttribute("font").trim();
        if(family.isEmpty())
        {
            return;
        }
        String styleStr = el.getAttribute("style").trim().toLowerCase(Locale.ROOT);
        int style = parseFontStyle(styleStr);
        String fontSizeStr = el.getAttribute("fontSize").trim();
        if(fontSizeStr.isEmpty())
        {
            String legacy = el.getAttribute("size").trim();
            if(!legacy.isEmpty() && !legacy.contains(","))
            {
                fontSizeStr = legacy;
            }
        }
        if(fontSizeStr.isEmpty())
        {
            throw new IllegalArgumentException("font requires fontSize");
        }
        int fontSize = Integer.parseInt(fontSizeStr);
        c.setFont(new Font(family, style, fontSize));
    }

    private static int parseFontStyle(String styleStr)
    {
        switch(styleStr)
        {
            case "plain":
                return Font.PLAIN;
            case "bold":
                return Font.BOLD;
            case "italic":
                return Font.ITALIC;
            case "bolditalic", "bold_italic":
                return Font.BOLD | Font.ITALIC;
            default:
                throw new IllegalArgumentException("Unknown font style: " + styleStr);
        }
    }

    private static int parseHorizontalAlignment(String ha)
    {
        switch(ha)
        {
            case "center":
                return SwingConstants.CENTER;
            case "left":
                return SwingConstants.LEFT;
            case "right":
                return SwingConstants.RIGHT;
            case "leading":
                return SwingConstants.LEADING;
            case "trailing":
                return SwingConstants.TRAILING;
            default:
                throw new IllegalArgumentException("Unknown horizontalAlignment: " + ha);
        }
    }
}
