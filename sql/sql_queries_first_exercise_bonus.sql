--addCitizen
INSERT INTO Citizens (id, firstName, lastName, gender, birthDate, taxId, address) VALUES (?, ?, ?, ?, CONVERT(DATE, ?, 105), ?, ?);

--deleteCitizen
DELETE FROM Citizens WHERE id = ?;

--updateCitizen
UPDATE Citizens
            SET 
                taxId = CASE WHEN ? in ('', NULL) THEN '' ELSE ? END, 
                address = ?
            WHERE id = ?
			;

--searchCitizens
SELECT id, firstName, lastName, gender, format(birthDate, 'dd-MM-yyyy') as birthDate, taxId, address FROM Citizens WHERE ...; --> sto phgaio kwdika, epeita apo to where clause, sumplhrwnw oti eprokeito na perasei o xrhsths

--printAllCitizens
SELECT id, firstName, lastName, gender, format(birthDate, 'dd-MM-yyyy') as birthDate, taxId, address FROM Citizens;

--createDatabaseSQL
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'app')
                    BEGIN
                        CREATE DATABASE app;
                    END

--createTableSQL
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Citizens' AND xtype='U') --> tsekarw an uparxei o table o Citizens. An den uparxei tote eketeleitai to parakatw create query
CREATE TABLE Citizens (
    id VARCHAR(8) PRIMARY KEY, --> to id column einai to primary key tou table
    firstName NVARCHAR(50) NOT NULL,
    lastName NVARCHAR(50) NOT NULL,
    gender NVARCHAR(6) NOT NULL,
    birthDate DATE NOT NULL,
    taxId VARCHAR(9) UNIQUE NULL, --> epipleon, ekana to column auto na periexei unique values, ka8ws to AFM einai monadiko se ka8e polith
    address NVARCHAR(100) NULL,
	CONSTRAINT check_id_length CHECK (len(id)=8), --> constraint gia na eksasfalisw to id length
	CONSTRAINT check_taxId_length CHECK (len(taxId)=9 OR len(taxId) IS NULL OR len(taxId)=0), --> constraint gia na eksasfalisw to taxid length
	CONSTRAINT check_gender_value CHECK (lower(gender) IN ('male', 'female')), --> constraint gia na tsekarw to value ths sthlhs gender
);; 
--> Skefthka sto parapanw createTableSQL, na ekana to taxId foreign key, alla to afhsa ka8ws de htan apaitoumeno, opote exw to periorismo UNIQUE

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'Citizens_TaxId' AND object_id = OBJECT_ID('Citizens')) --> tsekarw an uparxei o index me onoma Citizens_TaxId. An den uparxei tote eketeleitai to parakatw create query
BEGIN
		CREATE UNIQUE INDEX Citizens_TaxId ON Citizens(taxId)
		WHERE taxId IS NOT NULL AND taxId <> ''; --> Dhmiourghsa auto ton index, ka8ws den ebriska allo tropo sto na apotrepw thn apo8hkeush mh egkurwn timwn. An einai la8os, enhmerwste me
END;