<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/*[1]">
		<Datalogging>
			<xsl:attribute name="tagName">
                 <xsl:value-of select="name()" />
            </xsl:attribute>
			<xsl:for-each select="*">
				<DataloggingItemSet>
					<xsl:attribute name="id">
                        <xsl:value-of select="name()" />
                    </xsl:attribute>
					<xsl:attribute name="dateTime">
                        <xsl:value-of select="@_" />
                    </xsl:attribute>
					<xsl:for-each select="*">
						<DataloggingItem>
							<xsl:attribute name="id">
	                        <xsl:value-of select="name()" />
	                    </xsl:attribute>
							<xsl:attribute name="value">
	                        <xsl:value-of select="@_" />
	                    </xsl:attribute>
						</DataloggingItem>
					</xsl:for-each>
				</DataloggingItemSet>
			</xsl:for-each>
		</Datalogging>
	</xsl:template>
</xsl:stylesheet>

