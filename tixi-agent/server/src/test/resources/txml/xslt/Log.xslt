<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/*[1]">
		<Log>
			<xsl:attribute name="id">
                 <xsl:value-of select="name()" />
            </xsl:attribute>
			<xsl:for-each select="*">
				<Record>
					<xsl:attribute name="id">
                        <xsl:value-of select="name()" />
                    </xsl:attribute>
					<xsl:attribute name="dateTime">
                        <xsl:value-of select="@_" />
                    </xsl:attribute>
					<xsl:for-each select="*">
						<RecordItem>
							<xsl:attribute name="id">
	                        <xsl:value-of select="name()" />
	                    </xsl:attribute>
							<xsl:attribute name="value">
	                        <xsl:value-of select="@_" />
	                    </xsl:attribute>
						</RecordItem>
					</xsl:for-each>
				</Record>
			</xsl:for-each>
		</Log>
	</xsl:template>
</xsl:stylesheet>

