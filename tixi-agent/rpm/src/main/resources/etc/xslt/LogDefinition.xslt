<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <LogDefinition>
            <xsl:for-each select="/GetConfig/LogDefinition/LogFiles | /LogDefinition/LogFiles">
                <RecordIds>
                    <xsl:for-each select="current()/*[@record]">
                        <RecordId>
                            <xsl:attribute name="id">
                                <xsl:value-of select="@record" />
                            </xsl:attribute>
                        </RecordId>
                    </xsl:for-each>
                </RecordIds>
            </xsl:for-each>
            <RecordDefinitions>
                <xsl:for-each select="GetConfig/LogDefinition/Records/* | /LogDefinition/Records/*">
                    <RecordDefinition>
                        <xsl:attribute name="id">
                          <xsl:value-of select="name()" />
                        </xsl:attribute>
                        <recordItemDefinitions>
                            <xsl:for-each select="current()/*">
                                <RecordItemDefinition>
                                    <xsl:copy-of select="@*" />
                                    <xsl:attribute name="id">
                                        <xsl:value-of select="name()" />
                                    </xsl:attribute>
                                </RecordItemDefinition>
                            </xsl:for-each>
                        </recordItemDefinitions>
                    </RecordDefinition>
                </xsl:for-each>
            </RecordDefinitions>
        </LogDefinition>
    </xsl:template>
</xsl:stylesheet>

