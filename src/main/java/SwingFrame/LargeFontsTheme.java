package SwingFrame;

import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

/**
 ****************************************************************
 ****************************************************************
 * A Metal Look and Feel theme that sets the font size for all Look and
 * Feel fonts.
 * @author  Mats Luthman
 ****************************************************************
 ****************************************************************/

class LargeFontsTheme extends DefaultMetalTheme
{
    int m_fontSize;


    public LargeFontsTheme(int fontSize)
    {
        m_fontSize = fontSize;

        m_ControlTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_MenuTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_SubTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_SystemTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_UserTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_WindowTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
    }


    public String getName()
    {
        return "LargeFontsTheme";
    }

    public FontUIResource getControlTextFont()
    {
        return m_ControlTextFont;
    }


    public FontUIResource getMenuTextFont()
    {
        return m_MenuTextFont;
    }


    public FontUIResource getSubTextFont()
    {
        return m_SubTextFont;
    }


    public FontUIResource getSystemTextFont()
    {
        return m_SystemTextFont;
    }


    public FontUIResource getUserTextFont()
    {
        return m_UserTextFont;
    }


    public FontUIResource getWindowTextFont()
    {
        return m_WindowTextFont;
    }


    private FontUIResource m_ControlTextFont;
    private FontUIResource m_MenuTextFont;
    private FontUIResource m_SubTextFont;
    private FontUIResource m_SystemTextFont;
    private FontUIResource m_UserTextFont;
    private FontUIResource m_WindowTextFont;
}