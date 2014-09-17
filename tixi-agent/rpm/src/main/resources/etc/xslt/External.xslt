<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <External>
            <xsl:for-each select="/GetConfig/External/* | /External/*">
                <Bus>
                    <xsl:copy-of select="@*" />
                    <xsl:for-each select="*">
                        <Device>
                            <xsl:copy-of select="@*" />
                            <xsl:for-each select="*">
                                <Meter>
                                    <xsl:attribute name="id">
                                        <xsl:value-of select="name()" />
                                    </xsl:attribute>
                                </Meter>
                            </xsl:for-each>
                        </Device>
                    </xsl:for-each>
                </Bus>
            </xsl:for-each>
        </External>
    </xsl:template>
</xsl:stylesheet>
