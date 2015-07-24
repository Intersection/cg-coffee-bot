//
//  AppDelegate.swift
//  CoffeeBar
//
//  Created by Tim Mattison on 7/24/15.
//  Copyright (c) 2015 controlgroup. All rights reserved.
//

import Cocoa

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {
    let statusItem = NSStatusBar.systemStatusBar().statusItemWithLength(-2)

    @IBOutlet weak var window: NSWindow!

    func applicationDidFinishLaunching(aNotification: NSNotification) {
        loadButtonIcon()
        createMenu()
    }
    
    private func loadButtonIcon() {
        if let button = statusItem.button {
            button.image = NSImage(named: "StatusBarButtonImage")
        }
    }
    
    private func createMenu() {
        let menu = NSMenu()
        
        menu.addItem(NSMenuItem(title: "Quit CoffeeBar", action: Selector("terminate:"), keyEquivalent: "q"))
        
        statusItem.menu = menu
    }
}

