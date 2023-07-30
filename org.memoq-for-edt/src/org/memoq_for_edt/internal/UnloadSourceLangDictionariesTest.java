/**
 *
 */
package org.memoq_for_edt.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author dzhih
 *
 */
class UnloadSourceLangDictionariesTest
{
    /**
     * Test method for {@link org.memoq_for_edt.internal.UnloadSourceLangDictionaries#fixKeyInLine(java.lang.String, boolean)}.
     */
    @SuppressWarnings("nls")
    @Test
    void testFixKeyInLine()
    {
        UnloadSourceLangDictionaries testObject = new UnloadSourceLangDictionaries();

        String lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ a user\\ in\\ the\\ catalog\\..Lines=Cannot create a user in the catalog";
        assertEquals("Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ a\\ user\\ in\\ the\\ catalog\\..Lines=Cannot create a user in the catalog", testObject.fixKeyInLine(lineToTest));

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ user\\..Lines=Cannot create a user";
        assertEquals("Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ user\\..Lines=Cannot create a user", testObject.fixKeyInLine(lineToTest));

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\=\\ user\\..Lines=Cannot create a user";
        assertEquals("Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\=\\ user\\..Lines=Cannot create a user", testObject.fixKeyInLine(lineToTest));

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot=";
        assertEquals("Method.CreateAdministrator.Var.ErrorText.NStr.Cannot=", testObject.fixKeyInLine(lineToTest));

        lineToTest = "Synonym=Users";
        assertEquals("Synonym=Users", testObject.fixKeyInLine(lineToTest));

        lineToTest = "Method.ExecuteItemReplacement.Var.ErrorText.NStr.Cannot save\\ register\\ records for\\ recorder\\ \"%1\"\\ to\\ record\\ set\\ \"%2\"\\:\\n%3.Lines=Cannot save register records";
        assertEquals("Method.ExecuteItemReplacement.Var.ErrorText.NStr.Cannot\\ save\\ register\\ records\\ for\\ recorder\\ \"%1\"\\ to\\ record\\ set\\ \"%2\"\\:\\n%3.Lines=Cannot save register records", testObject.fixKeyInLine(lineToTest));

        lineToTest = "Attribute.Invalid.ToolTip=The external user does not have access to the application but their data is still stored.\\nInvalid external users are hidden from the user lists.\\n";
        assertEquals("Attribute.Invalid.ToolTip=The external user does not have access to the application but their data is still stored.\\nInvalid external users are hidden from the user lists.\\n", testObject.fixKeyInLine(lineToTest));
    }
}
