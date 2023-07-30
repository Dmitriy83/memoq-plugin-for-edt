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
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals("Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ a\\ user\\ in\\ the\\ catalog\\..Lines=Cannot create a user in the catalog", lineToTest);

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ user\\..Lines=Cannot create a user";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals("Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ user\\..Lines=Cannot create a user", lineToTest);

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\=\\ user\\..Lines=Cannot create a user";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals("Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\=\\ user\\..Lines=Cannot create a user", lineToTest);

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot=";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals("Method.CreateAdministrator.Var.ErrorText.NStr.Cannot=", lineToTest);

        lineToTest = "Synonym=Users";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals("Synonym=Users", lineToTest);

        lineToTest = "Method.ExecuteItemReplacement.Var.ErrorText.NStr.Cannot save\\ register\\ records for\\ recorder\\ \"%1\"\\ to\\ record\\ set\\ \"%2\"\\:\\n%3.Lines=Cannot save register records";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals("Method.ExecuteItemReplacement.Var.ErrorText.NStr.Cannot\\ save\\ register\\ records\\ for\\ recorder\\ \"%1\"\\ to\\ record\\ set\\ \"%2\"\\:\\n%3.Lines=Cannot save register records", lineToTest);
    }
}
