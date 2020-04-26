package com.polaris.container.gateway.dnssec4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DNSKEYRecord;
import org.xbill.DNS.DNSSEC;
import org.xbill.DNS.ExtendedFlags;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Options;
import org.xbill.DNS.RRSIGRecord;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

/**
 * DNSSEC resolver and validator.
 */
public class DnsSec {

    private static final Logger log = LoggerFactory.getLogger(DnsSec.class);
    
    {
    System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8,8.8.4.4");
    //System.setProperty("sun.net.spi.nameservice.nameservers", "75.75.75.75 ,75.75.75.76");
        
    // These are from https://www.dns-oarc.net/oarc/services/odvr
    //System.setProperty("sun.net.spi.nameservice.nameservers", "149.20.64.20,149.20.64.21");
    }
    
    /**
     * If the specified {@link InetSocketAddress} has not already resolved to
     * an IP address, this verifies the host name and returns a new, verified
     * and resolved {@link InetSocketAddress}. 
     * 
     * @param unresolved An unresolved {@link InetSocketAddress}.
     * @return The resolved and verified {@link InetSocketAddress}.
     * @throws DNSSECException If there's a problem with the DNS signature.
     * @throws IOException If there's a problem with the nameservers.
     */
    public static InetSocketAddress verify(final InetSocketAddress unresolved) 
        throws DNSSECException, IOException {
        if (!unresolved.isUnresolved()) {
            return unresolved;
        } 
        
        final InetAddress verified = getByName(unresolved.getHostName());
        return new InetSocketAddress(verified, unresolved.getPort());
    }
    
    /**
     * Access the specified URL and verifies the signatures of DNSSEC responses
     * if they exist, returning the resolved IP address. 
     * 
     * @param name The name of the site.
     * @return The IP address for the specified domain, verified if possible.
     * @throws IOException If there's an IO error accessing the nameservers or
     * sending or receiving messages with them.
     * @throws DNSSECException If there's a DNS error verifying the signatures
     * for any domain.
     */
    public static InetAddress getByName(final String name) 
        throws DNSSECException, IOException {
        final Name full = Name.concatenate(Name.fromString(name), Name.root);

        System.out.println("Verifying record: "+ full);
        //final String [] servers = ResolverConfig.getCurrentConfig().servers();
        final Resolver res = newResolver();
        final Record question = Record.newRecord(full, Type.A, DClass.IN);
        final Message query = Message.newQuery(question);
        System.out.println("Sending query...");
        final Message response = res.send(query);
        System.out.println("RESPONSE: "+response);
        final RRset[] answer = response.getSectionRRsets(Section.ANSWER);
        
        final ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
        for (final RRset set : answer) {
            System.out.println("\n;; RRset to chase:");

            // First check for a CNAME and target.
            Iterator<Record> rrIter = set.rrs();
            boolean hasCname = false;
            Name cNameTarget = null;
            while (rrIter.hasNext()) {
                final Record rec = rrIter.next();
                final int type = rec.getType();
                
                if (type == Type.CNAME) {
                    final CNAMERecord cname = (CNAMERecord) rec;
                    hasCname = true;
                    cNameTarget = cname.getTarget();
                } 
            }
            
            rrIter = set.rrs();
            while (rrIter.hasNext()) {
                final Record rec = rrIter.next();
                System.out.println(rec);
                final int type = rec.getType();
                if (type == Type.A) {
                    final ARecord arec = (ARecord) rec;
                    if (hasCname) {
                        if (rec.getName().equals(cNameTarget)) {
                            addresses.add(arec.getAddress());
                        }
                    } else {
                        addresses.add(arec.getAddress());
                    }
                }
            }
            final Iterator<Record> sigIter = set.sigs();
            while (sigIter.hasNext()) {
                final RRSIGRecord rec = (RRSIGRecord) sigIter.next();
                System.out.println("\n;; RRSIG of the RRset to chase:");
                System.out.println(rec);
                verifyZone(set, rec);
            }
        }
        return addresses.get(0);
    }

    private static void verifyZone(final RRset set, final RRSIGRecord record) 
        throws DNSSECException, IOException {
        System.out.println("\nLaunch a query to find a RRset of type DNSKEY for zone: "+record.getSigner());

        //System.out.println("\nVerifying sig for rec: "+record);
        //final DNSKEYRecord publicKey = signingKeyForRecord (record);
        
        final Name signer = record.getSigner();
        final int tag = record.getFootprint();
        System.out.println("Looking for tag: "+tag);
        
        boolean keyVerified = false;
        DNSKEYRecord keyRec = null;
        
        // We need to perform a multiline query to get the tags associated with
        // keys, which lets us verify records with the correct key.
        try {
            Options.set("multiline");
            final Resolver res = newResolver();
            
            final Record question = Record.newRecord(signer, Type.DNSKEY, DClass.IN);
            final Message query = Message.newQuery(question);
            final Message response = res.send(query);
            System.out.println("Sent query...");
            
            final RRset[] answer = response.getSectionRRsets(Section.ANSWER);
            for (final RRset answerSet : answer) {
                System.out.println("\n;; DNSKEYset that signs the RRset to chase:");
                //System.out.println(set);
                final Iterator<Record> rrIter = answerSet.rrs();
                while (rrIter.hasNext()) {
                    final Record rec = rrIter.next();
                    System.out.println(rec);
                    if (rec instanceof DNSKEYRecord) {
                        final DNSKEYRecord dnskKeyRec = (DNSKEYRecord) rec;
                        if (dnskKeyRec.getFootprint() == tag) {
                            System.out.println("\n\nFound matching DNSKEY for tag!! "+tag+"\n\n");
                            keyRec = dnskKeyRec;
                        }
                    }
                }
                System.out.println("\n;; RRSIG of the DNSKEYset that signs the RRset to chase:");
                final Iterator<Record> sigIter = answerSet.sigs();
                while (sigIter.hasNext()) {
                    final RRSIGRecord rec = (RRSIGRecord) sigIter.next();
                    System.out.println(rec);
                    
                    // This resource record set could be self-signed. Verify
                    // the signature as we go, and we'll validate the DS record
                    // as well later.
                    if (rec.getFootprint() == tag) {
                        DNSSEC.verify(answerSet, rec, keyRec);
                        keyVerified = true;
                    }
                }
                if (!keyVerified) {
                    log.info("DNSKEY not verified");
                    //throw new IOException("Key not verified!!");
                } else {
                    log.info("DNSKEY verified!!");
                }
                keyVerified = false;
            }

            if (keyRec == null) {
                throw new DNSSECException("Did not find DNSKEY record matching tag: "+tag);
            }
        } catch (final org.xbill.DNS.DNSSEC.DNSSECException e) {
            throw new DNSSECException("Error verifying record", e);
        } finally {
            Options.unset("multiline");
        }
        try {
            DNSSEC.verify(set, record, keyRec);
        } catch (final org.xbill.DNS.DNSSEC.DNSSECException e) {
            throw new DNSSECException("Error verifying record", e);
        }
        
        verifyDsRecordForSignerOf(record);
    }

    private static Resolver newResolver() throws UnknownHostException {
        final Resolver res = new ExtendedResolver();
        res.setEDNS(0, 0, ExtendedFlags.DO, null);
        res.setIgnoreTruncation(false);
        //res.setTCP(true);
        
        // Timeouts are in seconds.
        res.setTimeout(15);
        return res;
    }

    private static void verifyDsRecordForSignerOf(final RRSIGRecord rec) 
        throws IOException, DNSSECException {
        final Name signer = rec.getSigner();
        System.out.println("\nLaunch a query to find a RRset of type DS for zone: "+signer);
        final Resolver res = newResolver();

        final Record question = Record.newRecord(signer, Type.DS, DClass.IN);
        final Message query = Message.newQuery(question);
        final Message response = res.send(query);
        System.out.println("Sent query and got response: "+response);
        
        final RRset[] answer = response.getSectionRRsets(Section.ANSWER);
        for (final RRset set : answer) {
            final Iterator<Record> rrIter = set.rrs();
            System.out.println("\n;; DSset of the DNSKEYset");
            while (rrIter.hasNext()) {
                System.out.println(rrIter.next());
            }
            final Iterator<Record> sigIter = set.sigs();
            System.out.println("\n;; RRSIG of the DSset of the DNSKEYset");
            while (sigIter.hasNext()) {
                final Record sigRec = sigIter.next();
                System.out.println(sigRec);
                if (sigIter.hasNext()) {
                    throw new IOException("We don't handle more than one RRSIGRecord for DS responses!!");
                }
                if (sigRec instanceof RRSIGRecord) {
                    final RRSIGRecord rr = (RRSIGRecord) sigRec;
                    
                    System.out.println(";; Now, we want to validate the DS :  recursive call");
                    verifyZone(set, rr);
                } 
            }
        }
        
        System.out.println(";; Out of recursive call");
    }
}
