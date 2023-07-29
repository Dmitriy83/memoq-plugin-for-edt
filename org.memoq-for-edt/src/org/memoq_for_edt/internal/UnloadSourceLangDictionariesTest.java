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

        String lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create user\\..Lines=Cannot create a user";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals(lineToTest, "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ user\\..Lines=Cannot create a user");

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ user\\..Lines=Cannot create a user";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals(lineToTest, "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\ user\\..Lines=Cannot create a user");

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\=\\ user\\..Lines=Cannot create a user";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals(lineToTest, "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot\\ create\\=\\ user\\..Lines=Cannot create a user");

        lineToTest = "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot=";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals(lineToTest, "Method.CreateAdministrator.Var.ErrorText.NStr.Cannot=");

        lineToTest = "Synonym=Users";
        lineToTest = testObject.fixKeyInLine(lineToTest);

        assertEquals(lineToTest, "Synonym=Users");
    }
}