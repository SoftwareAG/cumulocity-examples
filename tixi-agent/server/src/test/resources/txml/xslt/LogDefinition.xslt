<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<LogDefinition>
			<Records>
				<xsl:for-each select="GetConfig/LogDefinition/Records/*">
					<LogDefinitionItemSet>
						<xsl:attribute name="id">
		                  <xsl:value-of select="name()" />
		                </xsl:attribute>
						<LogDefinitionItems>
							<xsl:for-each select="current()/*">
								<LogDefinitionItem>
									<xsl:copy-of select="@*" />
									<xsl:attribute name="id">
				                        <xsl:value-of select="name()" />
				                    </xsl:attribute>
								</LogDefinitionItem>
							</xsl:for-each>
						</LogDefinitionItems>
					</LogDefinitionItemSet>
				</xsl:for-each>
			</Records>
		</LogDefinition>
	</xsl:template>
</xsl:stylesheet>

