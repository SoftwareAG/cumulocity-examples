<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<LogDefinition>
			<Records>
				<xsl:for-each select="LogDefinition/Records/*">
					<Datalogging>
						<xsl:attribute name="id">
		                  <xsl:value-of select="name()" />
		                </xsl:attribute>
						<xsl:for-each select="current()/*">
							<DataloggingItems>
								<DataloggingItem>
									<xsl:copy-of select="@*" />
									<xsl:attribute name="id">
				                        <xsl:value-of select="name()" />
				                    </xsl:attribute>
								</DataloggingItem>
							</DataloggingItems>
						</xsl:for-each>
					</Datalogging>
				</xsl:for-each>
			</Records>
		</LogDefinition>
	</xsl:template>
</xsl:stylesheet>

