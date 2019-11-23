
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Decryption {

    public static void main(String[] args) throws IOException {
        Decryption d = new Decryption();
        d.AES("File.enc", "Filed.txt", "akshaydighorikarsnehaldighorikar");
    }

    public boolean AES(String sourcePath, String destPath, String key) throws IOException {
        Decryption aes = new Decryption();

        FileInputStream fin = new FileInputStream(sourcePath);
        FileOutputStream fout = new FileOutputStream(destPath);

        byte[] byteKey = key.getBytes();
        byte[] input = new byte[16];
        byte[] output = new byte[16];
        int read;

        int round = fin.available() / 16;
        
        /*while ((read = fin.read()) != '?'){
            
        }*/

        while ((read = fin.read(input)) != -1) {
            output = aes.decrypt(input, byteKey, round);
            fout.write(output);
            round--;
        }
        fout.flush();
        fout.close();
        fin.close();
        return true;
    }


    public byte[] decrypt(byte[] fileBytes, byte[] key, int flag) {
        KeyExp keyE = new KeyExp();
        AESUTIL util = new AESUTIL();
        Decryption encrypt = new Decryption();

        int i = 0, j = 0, k = 0;
        int round = 14;

        int[][] state = new int[4][4];
        int[][] stateKey = new int[4][8];

        /// filling of state matrix with hex value
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                state[j][i] = util.charToIntHex(fileBytes[k]);
                k++;
            }
        }

        //filling state key
        k=0;
        for(i=0;i<8;i++){
            for(j=0;j<4;j++){
                stateKey[j][i] = util.charToIntHex(key[k]);
                k++;
            }
        }

        // step 1:- Add round key
        keyE.keyExpansion(stateKey, round);
        state = keyE.addRoundKey(state, 14);

        i = 13;
        while (i > 0) {
            encrypt.inShiftRows(state);
            encrypt.inSubBytes(state);
            state = keyE.addRoundKey(state, i);
            state = encrypt.inMixColumn(state);
            i--;
        }
        encrypt.inShiftRows(state);
        encrypt.inSubBytes(state);
        state = keyE.addRoundKey(state, 0);

        byte[] plainText = util.getPlainByte(state);
        if (flag == 1) {
            plainText = util.eliminatePadding(plainText);
        }

        return plainText;
    }

    void inShiftRows(int[][] state) {
        int i, j, k;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < i; j++) {
                int temp = state[i][3];
                for (k = 3; k > 0; k--) {
                    state[i][k] = state[i][k - 1];
                }
                state[i][k] = temp;
            }
        }
    }

    private void inSubBytes(int[][] state) {
        AESUTIL util = new AESUTIL();
        SBoxes sBox = new SBoxes();

        int i = 0;
        int j = 0;
        String co_x, co_y;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                String temp = util.hexToString(state[i][j]);
                char[] coordinates = temp.toCharArray();
                if (coordinates.length == 1) {
                    co_x = "0";
                    co_y = Character.toString(coordinates[0]);
                } else {
                    co_x = Character.toString(coordinates[0]);
                    co_y = Character.toString(coordinates[1]);
                }
                int x = util.convertToHex(co_x);
                int y = util.convertToHex(co_y);
                state[i][j] = sBox.I_SBOX[x][y];
            }
        }
    }

    int[][] inMixColumn(int[][] state) {
        SBoxes sBoxes = new SBoxes();
        AESUTIL util = new AESUTIL();

        int row, colm;
        int ans;
        int k = 0;
        int ansMat[][] = new int[4][4];

        for (k = 0; k < 4; k++) {
            for (row = 0; row < 4; row++) {
                ans = 0;
                for (colm = 0; colm < 4; colm++) {
                    ans = ans ^ util.multiplication(sBoxes.MATRIX[row][colm], state[colm][k]);
                }
                ansMat[row][k] = ans;
            }
        }
        return ansMat;
    }

    void addRoundKey(int[][] state, int[][] stateKey) {
        int i, j;
        int ans;
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                ans = state[j][i] ^ stateKey[j][i];
                state[j][i] = ans;
            }
        }
    }
}