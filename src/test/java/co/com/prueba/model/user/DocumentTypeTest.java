package co.com.prueba.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTypeTest {

    @Test
    void testDocumentType_Values() {
        DocumentType[] types = DocumentType.values();

        assertEquals(2, types.length);
        assertTrue(contains(types, DocumentType.P));
        assertTrue(contains(types, DocumentType.C));
    }

    @Test
    void testDocumentType_P() {
        DocumentType type = DocumentType.P;
        assertNotNull(type);
        assertEquals("P", type.name());
    }

    @Test
    void testDocumentType_C() {
        DocumentType type = DocumentType.C;
        assertNotNull(type);
        assertEquals("C", type.name());
    }

    @Test
    void testDocumentType_ValueOf() {
        DocumentType p = DocumentType.valueOf("P");
        DocumentType c = DocumentType.valueOf("C");

        assertEquals(DocumentType.P, p);
        assertEquals(DocumentType.C, c);
    }

    private boolean contains(DocumentType[] types, DocumentType type) {
        for (DocumentType t : types) {
            if (t == type) {
                return true;
            }
        }
        return false;
    }
}
